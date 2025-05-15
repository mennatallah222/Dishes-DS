package com.dishes.services;

import java.util.List;

import com.dishes.dto.OrderFailedEvent;
import com.dishes.entities.OrderFailure;

public interface OrderFailureService {
    void saveFailure(OrderFailedEvent event);
    List<OrderFailure> getAllFailures();
}