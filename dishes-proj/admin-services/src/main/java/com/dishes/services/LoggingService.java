package com.dishes.services;

import com.dishes.configs.LoggingRabbitMQConfig;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Stateless;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Stateless
public class LoggingService {
    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;
    private final String serviceName = "Admin";

    @PostConstruct
    public void initialize() {
        factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");
        
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(LoggingRabbitMQConfig.LOG_EXCHANGE, "topic", true);
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException("Failed to initialize RabbitMQ connection", e);
        }
    }

    @PreDestroy
    public void cleanup() {
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
            if (connection != null && connection.isOpen()) {
                connection.close();
            }
        } catch (IOException | TimeoutException e) {
            System.err.println("Failed to close RabbitMQ connection: " + e.getMessage());
        }
    }

    public void logInfo(String message) {
        sendLogMessage(serviceName + "_Info", message);
    }

    public void logWarning(String message) {
        sendLogMessage(serviceName + "_Warning", message);
    }

    public void logError(String message) {
        sendLogMessage(serviceName + "_Error", message);
    }

    private void sendLogMessage(String routingKey, String message) {
        try {
            channel.basicPublish(LoggingRabbitMQConfig.LOG_EXCHANGE, routingKey, null, message.getBytes());
        } catch (IOException e) {
            System.err.println("Failed to send log message: " + e.getMessage());
        }
    }
}