package com.dishes.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingRabbitMQConfig {
    public static final String LOG_EXCHANGE = "log";
    public static final String ADMIN_ERROR_QUEUE = "admin_error_queue";

    @Bean
    public TopicExchange logExchange() {
        return new TopicExchange(LOG_EXCHANGE, true, false);
    }

    @Bean
    public Queue adminErrorQueue() {
        return new Queue(ADMIN_ERROR_QUEUE, true);
    }

    @Bean
    public Binding adminErrorBinding() {
        return BindingBuilder.bind(adminErrorQueue())
                .to(logExchange())
                .with("*.Error");
    }
}