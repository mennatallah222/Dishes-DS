package com.dishes.controllers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dishes.dtos.AddDishRequest;
import com.dishes.dtos.ProductResponse;
import com.dishes.dtos.ProductSoldResponse;
import com.dishes.dtos.UpdateProductRequest;
import com.dishes.services.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/seller/products")
@PreAuthorize("hasRole('SELLER')")
public class ProductController {
    private final ProductService productService;
    public ProductController(ProductService productService) {
        this.productService=productService;
    }
    @PostMapping("/add-dish")
    public ResponseEntity<?> addDish(@RequestHeader("Authorization") String authHeader, @Valid @RequestBody AddDishRequest request) {
        try{
            System.out.println("Authentication: " + SecurityContextHolder.getContext().getAuthentication());

            ProductResponse dish = productService.addDish(request, authHeader);
            if (dish == null) {
                return ResponseEntity.badRequest().body("Dish with this name already exists");
            }
            return ResponseEntity.ok(dish);
        }
        catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("get-seller-dishes")
    public ResponseEntity<List<ProductResponse>> getSellerProducts(
            @RequestHeader("Authorization") String authHeader) {        
        List<ProductResponse> products = productService.getProductsBySeller(authHeader);
        return ResponseEntity.ok(products);
    }

    @GetMapping("get-sold-dishes")
    public ResponseEntity<Map<String, List<ProductSoldResponse>>> getSoldDishes(@RequestHeader("Authorization") String authHeader) {
        List<ProductSoldResponse> products = productService.getSoldProducts(authHeader);
        return ResponseEntity.ok(products.stream().collect(Collectors.groupingBy(sp->sp.getproductName()!=null?sp.getproductName():"Product name mapping failed / Unknown product")));
    }

    @GetMapping("get-available-dishes")
    public ResponseEntity<List<ProductResponse>> getAvailableDishes(@RequestHeader("Authorization") String authHeader) {
        List<ProductResponse> products = productService.getAvailableProducts(authHeader);
        return ResponseEntity.ok(products);
    }

    @PutMapping("/update-dish/{dishId}")
    public ResponseEntity<?> updateDish(@RequestHeader("Authorization") String authHeader, @PathVariable Long dishId, @Valid @RequestBody UpdateProductRequest request) {
        try {
            ProductResponse updatedDish = productService.updateDish(dishId, request, authHeader);
            if (updatedDish == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(updatedDish);
        }
        catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

}
