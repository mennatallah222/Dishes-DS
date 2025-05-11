package com.dishes.dtos;

public class AddDishRequest {
    private String name;
    private int amount;
    private double price;
    private String shippingCompanyName;
    
    public String getShippingCompanyName() {
        return shippingCompanyName;
    }
    public void setShippingCompanyName(String shippingCompanyName) {
        this.shippingCompanyName = shippingCompanyName;
    }
    public int getAmount() {
        return amount;
    }
    public void setAmount(int amount) {
        this.amount = amount;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
}
