package com.dishes.services;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.naming.ServiceUnavailableException;

import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.dishes.config.RabbitMQConfig;
import com.dishes.dtos.AddOrderDTO;
import com.dishes.dtos.OrderProcessedResponse;
import com.dishes.dtos.OrderResponse;
import com.dishes.dtos.OrderRollbackEvent;
import com.dishes.dtos.rmq.OrderItemEvent;
import com.dishes.dtos.rmq.OrderPlacedEvent;
import com.dishes.entities.Order;
import com.dishes.entities.OrderItem;
import com.dishes.entities.ShippingCompany;
import com.dishes.repositories.CustomerRepository;
import com.dishes.repositories.OrderRepository;
import com.dishes.repositories.ShippingCompanyRepository;

import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;

@Service
@Transactional
public class OrderService {
    private final ShippingCompanyRepository shippingCompanyRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;
    private final RestTemplate restTemplate;
    private final String sellerServiceURL;

    private final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
    private final ConcurrentHashMap<Long, CompletableFuture<OrderResponse>> pendingOrders=new ConcurrentHashMap<>();

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);


    private final CircuitBreaker circuitBreaker;
    public OrderService(ShippingCompanyRepository shippingCompanyRepository, 
                      CustomerRepository customerRepository, 
                      OrderRepository orderRepository, 
                      RabbitTemplate rabbitTemplate,
                      RestTemplate restTemplate,
                      CircuitBreakerFactory<?, ?> circuitBreakerFactory
                    ){
        this.shippingCompanyRepository = shippingCompanyRepository;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.restTemplate=restTemplate;
        this.sellerServiceURL="http://localhost:8082/seller/healthCheck";
        this.circuitBreaker=circuitBreakerFactory.create("sellerServiceCircuitBreaker");
    }

    @Scheduled(fixedRate = 30000)
    public void healthCheck() {
        (circuitBreaker).run(
            () -> checkSellerServiceHealth(),
            throwable -> {
                log.error("Health check failed", throwable);
                return false;
            }
        );
    }

    private boolean checkSellerServiceHealth() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            sellerServiceURL, 
            String.class
        );
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Health check returned non-200 status");
        }
        return true;
    }

    @Transactional(rollbackOn = Exception.class)
    public CompletableFuture<OrderResponse> addOrder(AddOrderDTO request, String authHeader) throws ServiceUnavailableException {

        CompletableFuture<OrderResponse> responseFuture=new CompletableFuture<>();
        if(!isSellerServiceAvailable()){
            OrderResponse response = new OrderResponse();
            response.setStatus("FAILED");
            response.setErrorMessage("Seller service is currently unavailable!😔");
            response.setErrorCode("SERVICE_UNAVAILABLE");
            responseFuture.complete(response);
            return responseFuture;
        }

        Order order = new Order();
        order.setCustomer(customerRepository.findById(request.getCustomerId()).orElseThrow());
        order.setStatus(Order.OrderStatus.Pending);
        

        List<OrderItem> items = request.getItems().stream()
            .map(itemDto -> {
                OrderItem item = new OrderItem();
                item.setProductId(itemDto.getProductId());
                item.setQuantity(itemDto.getQuantity());
                item.setSellerId(itemDto.getSellerId());
                item.setProductName(itemDto.getProductName());
                item.setOrder(order);
                item.setPrice(itemDto.getPrice());
                order.addItem(item);
                return item;
            })
            .toList();
        
        order.setItems(items);
        double total=0.0;
        List<OrderItem> oi=order.getItems();
        for (OrderItem orderItem : oi) {
            total+=orderItem.getPrice()*orderItem.getQuantity();
        }
        order.setTotal(total);

        String companyName = request.getShippingCompanyName().trim().toLowerCase();
        ShippingCompany shippingCompany = shippingCompanyRepository.findByUniqueName(companyName)
                .orElseGet(() -> {
                    ShippingCompany newCompany = new ShippingCompany();
                    newCompany.setName(request.getShippingCompanyName().trim());
                    return shippingCompanyRepository.save(newCompany);
                });

        order.setShippingCompany(shippingCompany);
        Order savedOrder = orderRepository.save(order);
        pendingOrders.put(savedOrder.getId(), responseFuture);
        
        Order reloadedOrder = orderRepository.findById(savedOrder.getId()).orElseThrow();

        OrderPlacedEvent event = getOrderPlaceEvent(reloadedOrder);

        rabbitTemplate.convertAndSend(RabbitMQConfig.ORDERS_EXCHANGE, "order.placed", event, message ->{
                message.getMessageProperties().setReplyTo(RabbitMQConfig.ORDER_RESPONSES_QUEUE);
                message.getMessageProperties().setCorrelationId(UUID.randomUUID().toString());
                return message;
            });

        scheduledExecutor.schedule(() -> {
            if (!responseFuture.isDone()) {
                OrderResponse timeoutResponse=convertToResponse(reloadedOrder);
                timeoutResponse.setStatus("FAILED");
                timeoutResponse.setMessage("Order processing timed out");
                timeoutResponse.setUnavailableItems(Collections.emptyList());
                timeoutResponse.setErrorCode("TIMEOUT");
                responseFuture.complete(timeoutResponse);
                pendingOrders.remove(savedOrder.getId());

                reloadedOrder.setStatus(Order.OrderStatus.Failed);
                orderRepository.save(order);
            }
        }, 30, TimeUnit.SECONDS);

        return responseFuture;
    }

    OrderPlacedEvent getOrderPlaceEvent(Order o){
        OrderPlacedEvent event=new OrderPlacedEvent();
        event.setOrderId(o.getId());
        event.setCustomerId(o.getCustomer().getId());
        event.setCustomerName(o.getCustomer().getName());
        event.setCustomerEmail(o.getCustomer().getEmail());
        event.setShippingCompany(o.getShippingCompany().getName());
        event.setItems(o.getItems().stream()
                .map(item -> {
                    OrderItemEvent itemEvent = new OrderItemEvent();
                    itemEvent.setProductId(item.getProductId());
                    itemEvent.setProductName(item.getProductName());
                    itemEvent.setPrice(item.getPrice());
                    itemEvent.setQuantity(item.getQuantity());
                    itemEvent.setSellerId(item.getSellerId());
                    return itemEvent;
                })
                .toList());
        return event;
    }
    private boolean isSellerServiceAvailable() {
        try {
            restTemplate.getForEntity(sellerServiceURL, String.class);
            return true;
        }
        catch (ResourceAccessException e) {
            return false;
        }
    }
    
    
    @RabbitListener(queues = RabbitMQConfig.ORDER_RESPONSES_QUEUE)
    @Transactional
    public void handleOrderResponse(OrderProcessedResponse response) {

        CompletableFuture<OrderResponse> resFuture=pendingOrders.remove(response.getOrderId());

        if(resFuture!=null){
            Order order = orderRepository.findById(response.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found: " + response.getOrderId()));

            OrderResponse orderResponse=new OrderResponse();
            orderResponse.setId(order.getId());
            orderResponse.setShippingCompany(order.getShippingCompany().getName());
            orderResponse.setTotal(order.getTotal());

            if(response.isSuccess()) {
                order.setStatus(Order.OrderStatus.Completed);
                orderResponse.setStatus("COMPLETED");
                orderResponse.setMessage("Order is completed successfully! Items are reserved, proceed to checkout");
                orderRepository.saveAndFlush(order);
            }
            else{
                order.setStatus(Order.OrderStatus.Failed);
                System.out.println("Order failed: " + response.getMessage());
                orderResponse.setStatus("PENDING");
                orderResponse.setMessage(response.getMessage());
                orderResponse.setUnavailableItems(response.getItems());
                orderResponse.setErrorCode(response.getFailReason());
                orderRepository.saveAndFlush(order);
            }
            
            
            resFuture.complete(orderResponse);
            // sendOrderNotification(order.getCustomer().getId(), orderResponse);
        }

    }


    @Transactional
    public OrderResponse checkoutOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (order.getStatus() != Order.OrderStatus.Completed) {
            throw new IllegalStateException("Order is not in a checkout-able state");
        }

        try {
            double minCharge = getMinimumOrderCharge();
            OrderResponse response = new OrderResponse();
            response.setId(order.getId());
            response.setTotal(order.getTotal());
            
            if (order.getTotal() >= minCharge) {
                order.setStatus(Order.OrderStatus.Confirmed);
                response.setStatus("CONFIRMED");
                response.setMessage("Order confirmed successfully");
            }
            else{
                orderRollbackEvent(order);
                order.setStatus(Order.OrderStatus.Failed);
                response.setStatus("FAILED");
                response.setMessage(String.format(
                    "Order total %.2f is below minimum charge requirement of %.2f", 
                    order.getTotal(), 
                    minCharge
                ));
                response.setErrorCode("MIN_CHARGE_NOT_MET");
            }
            
            orderRepository.save(order);
            return response;
        }
        catch (Exception e) {
            orderRollbackEvent(order);
            order.setStatus(Order.OrderStatus.Failed);
            orderRepository.save(order);
            throw new RuntimeException("Checkout processing failed", e);
        }
    }

    private double getMinimumOrderCharge() {
        try {
            ResponseEntity<Double> response = restTemplate.getForEntity(
                "http://localhost:8080/admin-services/api/configs/min-order-charge", 
                Double.class
            );
            Double body = response.getBody();
            if (body == null) {
                log.error("Minimum order charge response body is null");
                throw new RuntimeException("Minimum order charge configuration is missing");
            }
            return body;
        }
        catch (Exception e) {
            log.error("Failed to fetch minimum order charge", e);
            throw new RuntimeException("Failed to fetch minimum order charge configuration");
        }
    }

    private void orderRollbackEvent(Order order) {
        try {
            OrderRollbackEvent event=new OrderRollbackEvent();
            event.setOrderId(order.getId());
            event.setItems(order.getItems().stream()
                .map(item -> {
                    OrderItemEvent itemEvent = new OrderItemEvent();
                    itemEvent.setProductId(item.getProductId());
                    itemEvent.setQuantity(item.getQuantity());
                    return itemEvent;
                })
                .toList());
            
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDERS_EXCHANGE, 
                "order.rollback", 
                event
            );
        }
        catch (Exception e) {
            log.error("Failed to send rollback request for order {}", order.getId(), e);
        }
    }

    private OrderResponse convertToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setStatus(order.getStatus().name());
        response.setShippingCompany(order.getShippingCompany().getName());
        response.setTotal(order.getTotal());
        response.setItems(
            order.getItems().stream()
                .map(item -> {
                    OrderItem itemDTO = new OrderItem();
                    itemDTO.setProductId(item.getProductId());
                    itemDTO.setProductName(item.getProductName());
                    itemDTO.setQuantity(item.getQuantity());
                    itemDTO.setPrice(item.getPrice());
                    itemDTO.setSellerId(item.getSellerId());
                    return itemDTO;
                })
                .toList()
        );
        return response;
    }

}