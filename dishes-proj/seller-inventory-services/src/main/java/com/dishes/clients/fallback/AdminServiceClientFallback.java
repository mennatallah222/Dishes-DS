package com.dishes.clients.fallback;

import org.springframework.stereotype.Component;

import com.dishes.clients.AdminServiceClient;
import com.dishes.dtos.SellerLoginErrorResponse;
import com.dishes.dtos.SellerLoginRequest;
import com.dishes.interfaces.SellerLoginResponse;

@Component
public class AdminServiceClientFallback implements AdminServiceClient {

    @Override
    public SellerLoginResponse authenticateSeller(SellerLoginRequest req) {
        SellerLoginErrorResponse response=new SellerLoginErrorResponse();
        response.setError("Login service is currently unavailable");
        response.setServiceAvailable(false);
        return response;
    }
    
}
