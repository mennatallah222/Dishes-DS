package com.dishes.controllers;

import com.dishes.dtos.SellerLoginRequest;
import com.dishes.dtos.SellerLoginResponse;
import com.dishes.services.SellerAuth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seller")
public class SellerController {

    private final SellerAuth authService;

    public SellerController(SellerAuth authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<SellerLoginResponse> login(@RequestBody SellerLoginRequest request) {
        SellerLoginResponse response = authService.authenticateSeller(request);
        return ResponseEntity.ok(response);
    }
}