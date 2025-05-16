package com.dishes.services;

import com.dishes.configs.LoggingRabbitMQConfig;
import com.dishes.entities.Log;
import com.rabbitmq.client.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@Singleton
@Startup
public class ErrorLogsConsumer {
    private Connection connection;
    private Channel channel;
    
    @PersistenceContext(unitName = "userPU")
    private EntityManager em;

    @PostConstruct
    public void initialize() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            connection = factory.newConnection();
            channel = connection.createChannel();

            String queue = "admin_error_queue";
            channel.queueDeclare(queue, true, false, false, null);
            channel.queueBind(queue, LoggingRabbitMQConfig.LOG_EXCHANGE, "*.Error");
            
            DeliverCallback dc = (consumerTag, delivery) -> {
                String msg = new String(delivery.getBody(), StandardCharsets.UTF_8);
                String routingKey = delivery.getEnvelope().getRoutingKey();
                String serviceName = routingKey.split("_")[0];
                Log log = new Log(serviceName, "ERROR", msg);
                em.persist(log);
            };
            
            channel.basicConsume(queue, true, dc, consumerTag -> {});
        }
        catch (IOException | TimeoutException e) {
            throw new RuntimeException("Failed to initialize error log listener", e);
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
        }
        catch (IOException | TimeoutException e) {
            System.err.println("Error cleaning up RabbitMQ resources: " + e.getMessage());
        }
    }
}