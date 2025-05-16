package com.dishes.startup;

import com.dishes.rabbitmq.PaymentFailedConsumer;
import com.dishes.services.ErrorLogsConsumer;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

@Startup
@Singleton
public class ConsumerInitializer {
    @EJB
    private PaymentFailedConsumer consumer;

    @EJB
    private ErrorLogsConsumer errorLogsConsumer;

    @PostConstruct
    public void init() {
        try {
            consumer.startConsumer();
            errorLogsConsumer.initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}