package com.dishes.dtos;

import java.util.List;

public class OrderProcessedResponse {
    private Long orderId;
    private boolean success;
    private String message;
    private String failReason;
    private List<OrderItemResponse> items;
    
    public OrderProcessedResponse(Long orderId, boolean success, String message, List<OrderItemResponse> items, String failReason) {
        this.orderId = orderId;
        this.success = success;
        this.message = message;
        this.items = items;
        this.failReason=failReason;
    }
    public OrderProcessedResponse() {
        
    }
    public String getFailReason() {
        return failReason;
    }
    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }
    public Long getOrderId() {
        return orderId;
    }
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public List<OrderItemResponse> getItems() {
        return items;
    }
    public void setItems(List<OrderItemResponse> items) {
        this.items = items;
    }
}