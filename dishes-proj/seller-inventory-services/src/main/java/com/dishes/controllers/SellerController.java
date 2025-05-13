package com.dishes.controllers;

import com.dishes.dtos.SellerLoginErrorResponse;
import com.dishes.dtos.SellerLoginRequest;
import com.dishes.dtos.SellerLoginSuccessResponse;
import com.dishes.interfaces.SellerLoginResponse;
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

    @GetMapping("/healthCheck")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Seller service is healthy!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody SellerLoginRequest request) {
        SellerLoginResponse response = authService.authenticateSeller(request);
        if(response instanceof SellerLoginErrorResponse error){
            if(!error.isServiceAvailable()){
                return ResponseEntity.status(503).body(response);
            }
            return ResponseEntity.badRequest().body(response);
        }
        else if(response instanceof SellerLoginSuccessResponse success){
            return ResponseEntity.ok(success);
        }
        return ResponseEntity.internalServerError().build();
    }
}