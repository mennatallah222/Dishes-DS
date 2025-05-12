package com.dishes.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Define exchange names
    public static final String ORDERS_EXCHANGE = "orders.exchange";
    public static final String SELLER_ORDERS_QUEUE = "seller.orders.queue";
    public static final String ORDER_RESPONSES_QUEUE = "order.responses.queue";

    @Bean
    public TopicExchange ordersExchange() {
        return new TopicExchange(ORDERS_EXCHANGE);
    }

    /*                               queues                                   */
    @Bean
    public Queue sellerOrdersQueue() {
        return new Queue(SELLER_ORDERS_QUEUE, true);
    }

    @Bean
    public Queue orderResponsesQueue() {
        return new Queue(ORDER_RESPONSES_QUEUE, true);
    }

    /*                               queues bindings                                   */
    
    @Bean
    public Binding sellerOrdersBinding(@Qualifier("sellerOrdersQueue") Queue sellerOrdersQueue, TopicExchange ordersExchange) {
        return BindingBuilder.bind(sellerOrdersQueue)
                .to(ordersExchange)
                .with("order.placed");
    }

    @Bean
    public Binding orderResponsesBinding(@Qualifier("orderResponsesQueue") Queue orderResponsesQueue, TopicExchange ordersExchange) {
        return BindingBuilder.bind(orderResponsesQueue)
                .to(ordersExchange)
                .with("order.response");
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
