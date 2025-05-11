package com.dishes.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dishes.entities.ShippingCompany;

public interface ShippingCompanyRepository extends JpaRepository<ShippingCompany, Long>{
    Optional<ShippingCompany> findByUniqueName(String uString);   
}