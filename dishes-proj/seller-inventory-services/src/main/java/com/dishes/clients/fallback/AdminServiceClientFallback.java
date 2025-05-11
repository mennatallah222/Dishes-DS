package com.dishes.clients.fallback;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.dishes.clients.AdminServiceClient;
import com.dishes.dtos.SellerLoginErrorResponse;
import com.dishes.dtos.SellerLoginRequest;

@Component
public class AdminServiceClientFallback implements AdminServiceClient {

    @Override
    public ResponseEntity<?> authenticateSeller(SellerLoginRequest req) {
        SellerLoginErrorResponse response=new SellerLoginErrorResponse();
        response.setError("Login service is currently unavailable");
        response.setServiceAvailable(false);
        return ResponseEntity.status(503).body(response);
    }
    
}
