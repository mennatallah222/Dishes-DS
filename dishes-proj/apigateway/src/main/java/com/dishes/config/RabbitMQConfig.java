package com.dishes.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Bean
    public DirectExchange orderFailureExchange() {
        return new DirectExchange("order.failure.exchange", true, false);
    }
    
    @Bean
    public Queue gatewayOrderFailureQueue() {
        return new Queue("gateway.failure.queue");
    }
    
    @Bean
    public Binding orderFailureBinding() {
        return BindingBuilder.bind(gatewayOrderFailureQueue())
                .to(orderFailureExchange())
                .with("PaymentFailed");
    }
}