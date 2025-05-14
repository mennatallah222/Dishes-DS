package com.dishes.services;

import com.dishes.dtos.OrderResponse;
import com.dishes.entities.Customer;
import com.dishes.entities.Order;
import com.dishes.entities.OrderItem;
import com.dishes.jwt.JwtTokenUtil;
import com.dishes.repositories.CustomerRepository;
import com.dishes.repositories.OrderRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private JwtTokenUtil token;



    public Optional<String> validate(Customer c) {
        if (c == null) return Optional.of("Customer object must not be null");
        if (!StringUtils.hasText(c.getEmail())) return Optional.of("Email must not be or empty");
        if (customerRepository.findByEmail(c.getEmail()).isPresent()) return Optional.of("Email already registered");
        return Optional.empty();
    }

    public Customer register(Customer c) {
        return customerRepository.save(c);
    }

    public List<Customer> getAllCustomers(){
        return customerRepository.findAll();
    }


    @Transactional
    public List<OrderResponse> getAllOrdersByCustomerId(String authHeader){
        Long customerId=token.extractCustomerId(authHeader.substring(7));

        return orderRepository.findByCustomerId(customerId).stream()
            .map(this::convertToResponse)
            .toList();
    }

    private OrderResponse convertToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setStatus(order.getStatus().name());
        response.setShippingCompany(order.getShippingCompany().getName());
        response.setTotal(order.getTotal());
        response.setItems(
            order.getItems().stream()
                .map(item -> {
                    OrderItem itemDTO = new OrderItem();
                    itemDTO.setProductId(item.getProductId());
                    itemDTO.setProductName(item.getProductName());
                    itemDTO.setQuantity(item.getQuantity());
                    itemDTO.setPrice(item.getPrice());
                    itemDTO.setSellerId(item.getSellerId());
                    return itemDTO;
                })
                .toList()
        );
        return response;
    }


}
