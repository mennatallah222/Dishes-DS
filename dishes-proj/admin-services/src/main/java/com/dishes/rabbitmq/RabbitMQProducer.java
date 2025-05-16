package com.dishes.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import jakarta.enterprise.context.ApplicationScoped;

import com.dishes.dto.CredentialsMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

@ApplicationScoped
public class RabbitMQProducer {

    private static final String EXCHANGE_NAME = "credentials_exchange";
    private static final String ROUTING_KEY = "company.credentials";

    private final ConnectionFactory factory;
    private final ObjectMapper objectMapper;

    public RabbitMQProducer() {
        factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");

        objectMapper = new ObjectMapper();
    }

    public void sendCredentialsMessage(CredentialsMessage msg) {
        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);
            String jsonMessage = objectMapper.writeValueAsString(msg);
            channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, jsonMessage.getBytes());
            System.out.println("Message sent: " + jsonMessage);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
