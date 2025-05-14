package com.dishes.config;

import org.springframework.amqp.core.Binding;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue sellerOrdersQueue() {
        return new Queue("seller.orders.queue", true);
    }
    
    @Bean
    public Queue rollbackQueue() {
        return new Queue("seller.orders.rollback.queue", true);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public TopicExchange ordersExchange() {
        return new TopicExchange("orders.exchange");
    }

    @Bean
    public Binding rollbackBinding(Queue rollbackQueue, TopicExchange ordersExchange) {
        return BindingBuilder
                .bind(rollbackQueue)
                .to(ordersExchange)
                .with("order.rollback");
    }

}