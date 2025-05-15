package com.dishes.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.dishes.client.AdminServiceClient;
import com.dishes.dto.OrderFailedEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderFailureHandler {
    private final AdminServiceClient adminServiceClient;

    @RabbitListener(queues = "gateway.failure.queue")
    public void handleOrderFailure(OrderFailedEvent event) {
        adminServiceClient.notifyAdmins(event);
    }
}