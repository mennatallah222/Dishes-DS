package com.dishes.services;

import com.dishes.entities.Log;
import com.rabbitmq.client.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.*;
import java.nio.charset.StandardCharsets;

@Singleton
@Startup
public class ErrorLogsConsumer {
    private static final String EXCHANGE_NAME = "log";
    private static final String QUEUE_NAME = "admin_error_queue";
    private static final String ROUTING_KEY = "Order_Error";

    private Connection connection;
    private Channel channel;

    @EJB
    private ErrorLogService errorLogService;

    @PostConstruct
    public void initialize() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            factory.setUsername("guest");
            factory.setPassword("guest");
            factory.setAutomaticRecoveryEnabled(true);

            this.connection = factory.newConnection();
            this.channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC, true);
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);
            channel.basicQos(1);

            System.out.println(" [*] Waiting for messages...");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                try {
                    String msg = new String(delivery.getBody(), StandardCharsets.UTF_8);
                    Log log = new Log(
                        delivery.getEnvelope().getRoutingKey().split("_")[0],
                        "ERROR",
                        msg
                    );
                    errorLogService.saveError(log);
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
                catch (Exception e) {
                    System.err.println("Error processing message:");
                    e.printStackTrace();
                    channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
                }
            };

            channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> {});
        }
        catch (Exception e) {
            System.err.println("Error initializing error logs consumer:");
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize error logs consumer", e);
        }
    }

    @PreDestroy
    public void cleanup() {
        try {
            if (channel != null && channel.isOpen()) channel.close();
            if (connection != null && connection.isOpen()) connection.close();
        }
        catch (Exception e) {
            System.err.println("Error cleaning up RabbitMQ resources:");
            e.printStackTrace();
        }
    }
}
