package com.dishes.clients;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.dishes.dtos.OrderItemResponse;
import com.dishes.dtos.OrderProcessedResponse;
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
        try {
            List<OrderItemResponse> responses = new ArrayList<>();
            
            for (OrderItemEvent item : event.getItems()) {
                Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));

                if (product.getAmount() >= item.getQuantity()) {

                    product.setAmount(product.getAmount() - item.getQuantity());
                    if(product.getAmount()==0){
                        product.setStatus(Product.ProductStatus.SOLD_OUT);
                    }
                    productRepository.save(product);

                    SoldProduct soldProduct = new SoldProduct();
                    soldProduct.setProductId(item.getProductId());
                    soldProduct.setProductName(product.getName());
                    soldProduct.setCustomerName(event.getCustomerName());
                    soldProduct.setCustomerEmail(event.getCustomerEmail());
                    soldProduct.setShippingCompany(event.getShippingCompany());
                    soldProduct.setPrice(item.getPrice());
                    soldProduct.setQuantity(item.getQuantity());
                    soldProduct.setSellerId(item.getSellerId());
                    soldProductRepository.save(soldProduct);

                    responses.add(new OrderItemResponse(item.getProductId(), true, "Success"));
                }
                else{
                    responses.add(new OrderItemResponse(
                        item.getProductId(), 
                        false, 
                        "Not enough product in stock. The available is: " + product.getAmount()
                    ));
                }
            }

            boolean allSuccess = responses.stream().allMatch(OrderItemResponse::isSuccess);
            
            MessageProperties props = message.getMessageProperties();
            rabbitTemplate.convertAndSend(
                props.getReplyTo(), 
                new OrderProcessedResponse(
                    event.getOrderId(), 
                    allSuccess, 
                    allSuccess ? "Order processed successfully" : "Some items are unavailable",
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
}