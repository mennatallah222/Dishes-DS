package com.dishes.repositories;

import com.dishes.entities.SoldProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SoldProductRepository extends JpaRepository<SoldProduct, Long> {
    List<SoldProduct> findBySellerId(Long sellerId);
}