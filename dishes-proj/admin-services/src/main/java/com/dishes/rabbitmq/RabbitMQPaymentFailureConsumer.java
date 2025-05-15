package com.dishes.rabbitmq;

import com.dishes.dtos.events.OrderFailedEvent;
import com.dishes.entities.OrderFailure;
import com.dishes.services.OrderFailureService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;

import java.nio.charset.StandardCharsets;

@Startup
@Singleton
public class RabbitMQPaymentFailureConsumer {

    private static final String EXCHANGE_NAME = "payments_exchange";
    private static final String ROUTING_KEY = "PaymentFailed";
    private static final String QUEUE_NAME = "payment_failed_queue";

    @Inject
    private OrderFailureService orderFailureService;

    @PostConstruct
    public void init() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            factory.setUsername("guest");
            factory.setPassword("guest");

            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT, true);
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);

            System.out.println("Waiting for PaymentFailed messages...");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String json = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println("Received PaymentFailed event: " + json);

                ObjectMapper mapper = new ObjectMapper();
                OrderFailedEvent event = mapper.readValue(json, OrderFailedEvent.class);

                OrderFailure failure = new OrderFailure();
                failure.setOrderId(event.getOrderId());
                failure.setCustomerId(event.getCustomerId());
                failure.setAmount(event.getTotalAmount());
                failure.setReason(event.getReason());
                failure.setTimestamp(event.getTimestamp());
                failure.setReceipt(event.getReceipt());

                orderFailureService.saveFailure(failure);
                System.out.println("Saved failure for order: " + event.getOrderId());
            };

            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
