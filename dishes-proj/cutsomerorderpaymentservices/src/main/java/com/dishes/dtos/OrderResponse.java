package com.dishes.dtos;

import java.util.List;

public class OrderResponse {
    private Long id;
    private double total;
    private String status;
    private String shippingCompany;
    private String message;
    private List<OrderItemResponse> unavailableItems;


    private String errorMessage;
    private String errorCode;
    
    
    public List<OrderItemResponse> getUnavailableItems() {
        return unavailableItems;
    }
    public void setUnavailableItems(List<OrderItemResponse> unavailableItems) {
        this.unavailableItems = unavailableItems;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public double getTotal() {
        return total;
    }
    public void setTotal(double total) {
        this.total = total;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getShippingCompany() {
        return shippingCompany;
    }
    public void setShippingCompany(String shippingCompany) {
        this.shippingCompany = shippingCompany;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    public String getErrorCode() {
        return errorCode;
    }
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
