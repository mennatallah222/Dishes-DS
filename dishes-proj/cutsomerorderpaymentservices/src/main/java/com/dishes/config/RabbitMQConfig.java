package com.dishes.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

import com.dishes.dtos.OrderProcessedResponse;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;

@Configuration
public class RabbitMQConfig {

    public static final String ORDERS_EXCHANGE = "orders.exchange";
    public static final String SELLER_ORDERS_QUEUE = "seller.orders.queue";
    public static final String ORDER_RESPONSES_QUEUE = "order.responses.queue";


    @Bean
    public DirectExchange paymentFailedExchange() {
        return new DirectExchange("PaymentFailed");
    }
    @Bean
    public Queue paymentFailedQueue(){
        return new Queue("payment.failed.queue", true);
    }
    @Bean
    public Binding paymentFailedBinding(@Qualifier("paymentFailedQueue")Queue q, @Qualifier ("paymentFailedExchange")DirectExchange e){
        return BindingBuilder.bind(q).to(e).with("PaymentFailed");
    }
    
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
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper() {
            @Override
            @NonNull
            public JavaType toJavaType(@NonNull MessageProperties properties) {
                //this is to convert to OrderProcessedResponse regardless of type info --> problem of dtos here and dto there :')
                return TypeFactory.defaultInstance().constructType(OrderProcessedResponse.class);
            }
        };
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }
    
}
