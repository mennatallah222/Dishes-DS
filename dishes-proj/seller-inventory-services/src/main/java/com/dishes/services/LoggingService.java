package com.dishes.services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dishes.config.LoggingRabbitMQConfig;

@Service
public class LoggingService {
    private final RabbitTemplate rabbitTemplate;
    private final String serviceName;

    public LoggingService(RabbitTemplate rabbitTemplate, 
                        @Value("${spring.application.name}") String serviceName) {
        this.rabbitTemplate = rabbitTemplate;
        this.serviceName = serviceName;
    }

    public void logInfo(String message) {
        sendLogMessage("Info", message);
    }

    public void logWarning(String message) {
        sendLogMessage("Warning", message);
    }

    public void logError(String message) {
        sendLogMessage("Error", message);
    }

    private void sendLogMessage(String level, String message) {
        String routingKey = serviceName + "_" + level;
        rabbitTemplate.convertAndSend(LoggingRabbitMQConfig.LOG_EXCHANGE, routingKey, message);
    }
}