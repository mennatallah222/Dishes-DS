package com.dishes.services;

import org.springframework.stereotype.Service;
import com.dishes.clients.AdminServiceClient;
import com.dishes.dtos.SellerLoginErrorResponse;
import com.dishes.dtos.SellerLoginRequest;
import com.dishes.interfaces.SellerLoginResponse;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

@Service
public class SellerAuth{
    private final AdminServiceClient adminServiceClient;
    private final CircuitBreaker circuitBreaker;

    public SellerAuth(AdminServiceClient adminServiceClient, CircuitBreakerRegistry circuitBreakerRegistry){
        this.adminServiceClient=adminServiceClient;
        this.circuitBreaker=circuitBreakerRegistry.circuitBreaker("adminService");
    }
    public SellerLoginResponse authenticateSeller(SellerLoginRequest req){
        if(circuitBreaker.getState()==CircuitBreaker.State.OPEN){
            return createMaintenanceResponse();
        }
        try{
            return adminServiceClient.authenticateSeller(req);
        }
        catch(Exception e){
            return createMaintenanceResponse();
        }
    }
    private SellerLoginErrorResponse createMaintenanceResponse(){
        SellerLoginErrorResponse response=new SellerLoginErrorResponse();
        response.setError("Login service is currently unavailable");
        response.setServiceAvailable(false);
        return response;
    }
}