package com.dishes.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dishes.dtos.AddDishRequest;
import com.dishes.dtos.ProductResponse;
import com.dishes.services.ProductService;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/seller/products")
public class ProductController {
    private final ProductService productService;
    public ProductController(ProductService productService) {
        this.productService=productService;
    }
    @PostMapping("/add-dish")
    // @PreAuthorize("hasRole('SELLER')")
    @Transactional
    public ResponseEntity<?> addDish(@RequestHeader("Authorization") String authHeader, @RequestBody AddDishRequest request) {
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
    // @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<List<ProductResponse>> getSellerProducts(
            @RequestHeader("Authorization") String authHeader) {        
        List<ProductResponse> products = productService.getProductsBySeller(authHeader);
        return ResponseEntity.ok(products);
    }
}
