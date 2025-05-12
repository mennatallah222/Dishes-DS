package com.dishes.services;

import java.util.List;
import java.util.UUID;

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
import com.dishes.dto.AddOrderDTO;
import com.dishes.dto.OrderProcessedResponse;
import com.dishes.dto.OrderResponse;
import com.dishes.dto.rmq.OrderItemEvent;
import com.dishes.dto.rmq.OrderPlacedEvent;
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

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);


    private final CircuitBreaker circuitBreaker;
    private volatile boolean isSellerServiceAvailable = false;

    public OrderService(ShippingCompanyRepository shippingCompanyRepository, 
                      CustomerRepository customerRepository, 
                      OrderRepository orderRepository, 
                      RabbitTemplate rabbitTemplate,
                      RestTemplate restTemplate,
                      CircuitBreakerFactory<?, ?> circuitBreakerFactory
                    ) {
        this.shippingCompanyRepository = shippingCompanyRepository;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.restTemplate=restTemplate;
        this.sellerServiceURL="http://localhost:8082";
        this.circuitBreaker=circuitBreakerFactory.create("sellerServiceCircuitBreaker");
    }

    @Scheduled(fixedRate = 30000)
    public void performHealthCheck() {
        isSellerServiceAvailable = (circuitBreaker).run(
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
    public OrderResponse addOrder(AddOrderDTO request, String authHeader) throws ServiceUnavailableException {
        if(!isSellerServiceAvailable()){
            OrderResponse response = new OrderResponse();
            response.setStatus("FAILED");
            response.setErrorMessage("Seller service is currently unavailable!😔");
            response.setErrorCode("SERVICE_UNAVAILABLE");
            return response;
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
        savedOrder = orderRepository.findById(savedOrder.getId()).orElseThrow();

        OrderPlacedEvent event = new OrderPlacedEvent();
        event.setOrderId(savedOrder.getId());
        event.setCustomerId(savedOrder.getCustomer().getId());
        event.setCustomerName(savedOrder.getCustomer().getName());
        event.setCustomerEmail(savedOrder.getCustomer().getEmail());
        event.setShippingCompany(savedOrder.getShippingCompany().getName());
        event.setItems(savedOrder.getItems().stream()
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

        rabbitTemplate.convertSendAndReceive(
            RabbitMQConfig.ORDERS_EXCHANGE, 
            "order.placed",
            event,
            message -> {
                message.getMessageProperties().setReplyTo(RabbitMQConfig.ORDER_RESPONSES_QUEUE);
                message.getMessageProperties().setCorrelationId(UUID.randomUUID().toString());
                return message;
            }
        );

        return convertToResponse(savedOrder);
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
    public void handleOrderResponse(OrderProcessedResponse response) {
        Order order = orderRepository.findById(response.getOrderId())
            .orElseThrow(() -> new RuntimeException("Order not found: " + response.getOrderId()));

        if(response.isSuccess()) {
            order.setStatus(Order.OrderStatus.Confirmed);
            orderRepository.save(order);
            //to send a confirmation notification here: ****TODO****
        }
        else{   //rollback the order actions
            order.setStatus(Order.OrderStatus.Failed);
            System.out.println("Order failed: " + response.getMessage());
            //to send a confirmation notification here: ****TODO****
        }
        
    }

    private OrderResponse convertToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setStatus(order.getStatus().name());
        response.setShippingCompany(order.getShippingCompany().getName());

        double total = order.getItems().stream()
            .mapToDouble(item -> item.getPrice() * item.getQuantity())
            .sum();

        response.setTotal(total);
        return response;
    }
}