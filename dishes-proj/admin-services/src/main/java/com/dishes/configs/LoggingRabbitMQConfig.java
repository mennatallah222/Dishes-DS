package com.dishes.configs;

import com.rabbitmq.client.*;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
@Singleton
@Startup
public class LoggingRabbitMQConfig {

    public static final String LOG_EXCHANGE = "log";
    private com.rabbitmq.client.Connection connection;

    @PostConstruct
    public void initialize() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            factory.setUsername("guest");
            factory.setPassword("guest");

            connection = factory.newConnection();
            try (Channel channel = connection.createChannel()) {
                channel.exchangeDeclare(LOG_EXCHANGE, BuiltinExchangeType.TOPIC, true);
            }
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException("Failed to initialize RabbitMQ logging configuration", e);
        }
    }
}
