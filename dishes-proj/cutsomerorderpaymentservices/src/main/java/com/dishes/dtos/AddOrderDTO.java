package com.dishes.dtos;

import java.util.List;

public class AddOrderDTO {
    private Long customerId;
    private String shippingCompanyName;
    private List<OrderItemDTO> items;
    public Long getCustomerId() {
        return customerId;
    }
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    public String getShippingCompanyName() {
        return shippingCompanyName;
    }
    public void setShippingCompanyName(String shippingCompanyName) {
        this.shippingCompanyName = shippingCompanyName;
    }
    public List<OrderItemDTO> getItems() {
        return items;
    }
    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }
    
}
