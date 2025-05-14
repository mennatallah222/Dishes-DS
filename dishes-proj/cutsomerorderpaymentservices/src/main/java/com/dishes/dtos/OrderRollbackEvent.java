package com.dishes.dtos;

import java.util.List;

import com.dishes.dtos.rmq.OrderItemEvent;

public class OrderRollbackEvent {
    private Long orderId;
    private List<OrderItemEvent> items;
    public Long getOrderId() {
        return orderId;
    }
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    public List<OrderItemEvent> getItems() {
        return items;
    }
    public void setItems(List<OrderItemEvent> items) {
        this.items = items;
    }
}