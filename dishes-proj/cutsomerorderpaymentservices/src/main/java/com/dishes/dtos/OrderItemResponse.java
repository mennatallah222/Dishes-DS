package com.dishes.dtos;

public class OrderItemResponse {
    private Long productId;
    private boolean success;
    private String message;

    public OrderItemResponse(Long productId, boolean success, String message) {
        this.productId = productId;
        this.success = success;
        this.message = message;
    }
    public OrderItemResponse() {
        
    }
    public Long getProductId() {
        return productId;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
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
}