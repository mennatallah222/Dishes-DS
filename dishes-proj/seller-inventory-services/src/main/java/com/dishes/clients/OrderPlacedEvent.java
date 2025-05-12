package com.dishes.clients;

import java.util.List;

public class OrderPlacedEvent {
    private Long orderId;
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private String shippingCompany;
    private List<OrderItemEvent> items;

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
    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public String getCustomerEmail() {
        return customerEmail;
    }
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
    public String getShippingCompany() {
        return shippingCompany;
    }
    public void setShippingCompany(String shippingCompany) {
        this.shippingCompany = shippingCompany;
    }
    public List<OrderItemEvent> getItems() {
        return items;
    }
    public void setItems(List<OrderItemEvent> items) {
        this.items = items;
    }
}