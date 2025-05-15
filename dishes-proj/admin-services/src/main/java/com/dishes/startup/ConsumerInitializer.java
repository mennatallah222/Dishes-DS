package com.dishes.startup;

import com.dishes.rabbitmq.PaymentFailedConsumer;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

@Startup
@Singleton
public class ConsumerInitializer {
    @EJB
    private PaymentFailedConsumer consumer;

    @PostConstruct
    public void init() {
        try {
            consumer.startConsumer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}