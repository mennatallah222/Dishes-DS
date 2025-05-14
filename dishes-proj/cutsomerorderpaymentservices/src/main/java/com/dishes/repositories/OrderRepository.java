package com.dishes.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dishes.entities.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{

    List<Order> findByCustomerId(Long customerId);

    
}
