package com.dishes.dtos;

public class OrderItemDTO {
    private Long productId;
    private String productName;
    private Long sellerId;
    private double price;
    private int quantity;
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public Long getProductId() {
        return productId;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    public Long getSellerId() {
        return sellerId;
    }
    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
}
