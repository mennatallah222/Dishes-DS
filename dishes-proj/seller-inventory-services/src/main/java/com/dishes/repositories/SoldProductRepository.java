package com.dishes.repositories;

import com.dishes.entities.SoldProduct;

import feign.Param;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SoldProductRepository extends JpaRepository<SoldProduct, Long> {
    List<SoldProduct> findBySellerId(Long sellerId);

    @Modifying
    @Query("DELETE FROM SoldProduct sp WHERE sp.productId = :productId AND sp.orderId = :orderId")
    void deleteByProductIdAndOrderId(@Param("productId") Long productId, @Param("orderId") Long orderId);
}