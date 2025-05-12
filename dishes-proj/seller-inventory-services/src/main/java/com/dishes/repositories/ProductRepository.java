package com.dishes.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dishes.entities.Product;
import com.dishes.entities.Seller;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
    //Jparepository class provides CRUD ops by default :)
    // like: save(), findById(), findAll(), deleteById()
    List<Product> findBySeller(Seller seller);
    List<Product> findBySellerId(Long sellerId);
    List<Product> findByStatus(Product.ProductStatus status);
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByPriceBetween(double minPrice, double maxPrice);
    boolean existsByNameAndSeller(String name, Seller seller);

    List<Product> findBySellerIdAndStatus(Long sellerId, Product.ProductStatus status);
    
}