package com.dishes.clients;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.dishes.dtos.OrderItemResponse;
import com.dishes.dtos.OrderProcessedResponse;
import com.dishes.dtos.OrderRollbackEvent;
import com.dishes.entities.Product;
import com.dishes.entities.SoldProduct;
import com.dishes.repositories.ProductRepository;
import com.dishes.repositories.SoldProductRepository;

import jakarta.transaction.Transactional;

@Component
public class OrderEventListener {
    private final SoldProductRepository soldProductRepository;
    private final ProductRepository productRepository;
    private final RabbitTemplate rabbitTemplate;

    private static final Logger log = LoggerFactory.getLogger(OrderRollbackEvent.class);

    public OrderEventListener(SoldProductRepository soldProductRepository, 
                            ProductRepository productRepository,
                            RabbitTemplate rabbitTemplate) {
        this.soldProductRepository = soldProductRepository;
        this.productRepository = productRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional
    @RabbitListener(queues = "seller.orders.queue")
    public void handleOrderPlaced(OrderPlacedEvent event, Message message) {
        List<OrderItemResponse> responses = new ArrayList<>();
        boolean isAllAvailable=true;
        try {
            List<Product> ordeProducts=new ArrayList<>();
            for (OrderItemEvent item : event.getItems()) {
                Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));

                if (product.getAmount()<item.getQuantity()) {
                    isAllAvailable=false;
                    responses.add(new OrderItemResponse(
                        item.getProductId(), 
                        false, 
                        "Not enough product in stock. The available is: " + product.getAmount()
                    ));
                }
                else{
                    responses.add(new OrderItemResponse(item.getProductId(), true, "Success"));
                    ordeProducts.add(product);
                }
            }

            if(isAllAvailable){
                for (int i = 0; i < event.getItems().size(); i++) {
                    OrderItemEvent oi=event.getItems().get(i);
                    Product product=ordeProducts.get(i);
                    product.setAmount(product.getAmount() - oi.getQuantity());
                    if(product.getAmount()==0){
                        product.setStatus(Product.ProductStatus.SOLD_OUT);
                    }
                    productRepository.save(product);

                    SoldProduct soldProduct = new SoldProduct();
                    soldProduct.setProductId(oi.getProductId());
                    soldProduct.setProductName(product.getName());
                    soldProduct.setCustomerName(event.getCustomerName());
                    soldProduct.setCustomerEmail(event.getCustomerEmail());
                    soldProduct.setShippingCompany(event.getShippingCompany());
                    soldProduct.setPrice(oi.getPrice());
                    soldProduct.setQuantity(oi.getQuantity());
                    soldProduct.setSellerId(oi.getSellerId());
                    soldProductRepository.save(soldProduct);
                }
                productRepository.saveAll(ordeProducts);
            }            
            MessageProperties props = message.getMessageProperties();
            rabbitTemplate.convertAndSend(
                props.getReplyTo(), 
                new OrderProcessedResponse(
                    event.getOrderId(), 
                    isAllAvailable, 
                    isAllAvailable ? "Order processed successfully" : "Some items are unavailable",
                    responses
                ),
                msg -> {
                    msg.getMessageProperties().setCorrelationId(props.getCorrelationId());
                    return msg;
                }
            );

        }
        catch (Exception e) {
            MessageProperties props = message.getMessageProperties();
            rabbitTemplate.convertAndSend(
                props.getReplyTo(), 
                new OrderProcessedResponse(
                    event.getOrderId(), 
                    false, 
                    "Processing failed: " + e.getMessage(),
                    Collections.emptyList()
                ),
                msg -> {
                    msg.getMessageProperties().setCorrelationId(props.getCorrelationId());
                    return msg;
                }
            );
        }
    }


    @Transactional
    @RabbitListener(queues = "seller.orders.rollback.queue")
    public void handleOrderRollback(OrderRollbackEvent event) {
        log.info("Processing rollback for order {}", event.getOrderId());
        
        for (OrderItemEvent item : event.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));

            product.setAmount(product.getAmount() + item.getQuantity());
            if (product.getStatus() == Product.ProductStatus.SOLD_OUT) {
                product.setStatus(Product.ProductStatus.AVAILABLE);
            }
            productRepository.save(product);

            soldProductRepository.deleteByProductIdAndOrderId(
                item.getProductId(),
                event.getOrderId()
            );
        }
    }
    
}