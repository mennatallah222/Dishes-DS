package com.dishes.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double total;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items=new ArrayList<>();
    
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="shipping_company_id")
    private ShippingCompany shippingCompany;
    
    public List<OrderItem> getItems() {
        return items;
    }
    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
    public ShippingCompany getShippingCompany() {
        return shippingCompany;
    }
    public void setShippingCompany(ShippingCompany shippingCompany) {
        this.shippingCompany = shippingCompany;
    }
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public OrderStatus getStatus() {
        return status;
    }
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
    
    public void calculateAndSetTotal() {
        this.total = items.stream()
        .mapToDouble(item -> item.getPrice() * item.getQuantity())
        .sum();
    }
    
    public double getTotal() {
        return total;
    }

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
        calculateAndSetTotal();
    }
    public void setTotal(double total) {
        this.total = total;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    
    public enum OrderStatus {
        Pending("available"),
        Cancelled("cancelled"),
        Confirmed("confirmed"),
        Completed("completed"),
        Failed("failed");
        
        private final String value;
        OrderStatus(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }
    
}


