package com.dishes.services;

import org.springframework.stereotype.Service;
import com.dishes.clients.AdminServiceClient;
import com.dishes.dtos.SellerLoginRequest;
import com.dishes.dtos.SellerLoginResponse;

@Service
public class SellerAuth{
    private final AdminServiceClient adminServiceClient;

    public SellerAuth(AdminServiceClient adminServiceClient){
        this.adminServiceClient=adminServiceClient;
    }
    public SellerLoginResponse authenticateSeller(SellerLoginRequest req){
        return adminServiceClient.authenticateSeller(req);
    }
}