package com.dishes.services;

import com.dishes.entities.Customer;
import com.dishes.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public Optional<String> validate(Customer c) {
        if (c == null) return Optional.of("Customer object must not be null");
        if (!StringUtils.hasText(c.getEmail())) return Optional.of("Email must not be null or empty");
        if (customerRepository.findByEmail(c.getEmail()).isPresent()) return Optional.of("Email already registered");
        return Optional.empty();
    }

    public Customer register(Customer c) {
        return customerRepository.save(c);
    }
}
