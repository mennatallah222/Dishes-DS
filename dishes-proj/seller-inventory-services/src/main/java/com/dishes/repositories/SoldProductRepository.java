package com.dishes.repositories;

import com.dishes.entities.SoldProduct;

import feign.Param;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SoldProductRepository extends JpaRepository<SoldProduct, Long> {
    List<SoldProduct> findBySellerId(Long sellerId);

    @Modifying
    @Transactional
    @Query("DELETE FROM SoldProduct sp WHERE sp.orderId = :orderId")
    int deleteByOrderId(@Param("orderId") Long orderId);


}