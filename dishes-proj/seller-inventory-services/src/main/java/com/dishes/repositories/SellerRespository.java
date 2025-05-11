package com.dishes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dishes.entities.Seller;

@Repository
public interface SellerRespository extends JpaRepository<Seller, Long>{
    
}
