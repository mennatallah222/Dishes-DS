package com.dishes.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dishes.dtos.ProductResponse;
import com.dishes.services.ProductService;

@RestController
@RequestMapping("/public/products")
public class PublicProductController {
    private final ProductService productService;
    
    public PublicProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/get-all-products")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/get-all-available-products")
    public ResponseEntity<List<ProductResponse>> getAllAvailableProducts() {
        List<ProductResponse> products = productService.getAllAvailableProducts();
        return ResponseEntity.ok(products);
    }
}