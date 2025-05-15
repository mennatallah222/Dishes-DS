package com.dishes.dtos.events;

import java.time.LocalDateTime;

public class OrderFailedEvent {
    private Long orderId;
    private Long customerId;
    private double totalAmount;
    private String reason;
    private LocalDateTime timestamp;

    public OrderFailedEvent() {}

    public OrderFailedEvent(Long orderId, Long customerId, double totalAmount, String reason, LocalDateTime timestamp) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.reason = reason;
        this.timestamp = timestamp;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}