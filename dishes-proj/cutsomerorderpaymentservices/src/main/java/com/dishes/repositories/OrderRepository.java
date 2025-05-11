package com.dishes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dishes.entities.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{

    
}
