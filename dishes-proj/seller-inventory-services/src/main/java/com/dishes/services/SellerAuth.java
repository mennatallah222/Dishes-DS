package com.dishes.services;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.dishes.clients.AdminServiceClient;
import com.dishes.dtos.SellerLoginErrorResponse;
import com.dishes.dtos.SellerLoginRequest;
import com.dishes.dtos.SellerLoginSuccessResponse;
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
            ResponseEntity<?> response=adminServiceClient.authenticateSeller(req);
            if(response.getStatusCode().is2xxSuccessful()){
                Object body=response.getBody();
                if(body instanceof Map<?, ?>mb){
                    SellerLoginSuccessResponse success=new SellerLoginSuccessResponse();
                    success.setToken((String)mb.get("token"));
                    return success;
                }
            }
            else{
                Object body=response.getBody();
                if(body instanceof Map<?, ?>mb){
                    SellerLoginErrorResponse error=new SellerLoginErrorResponse();
                    error.setError((String)mb.get("error"));
                    error.setServiceAvailable(false);
                    return error;
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
            return createMaintenanceResponse();
        }
        return createMaintenanceResponse();
    }
    private SellerLoginErrorResponse createMaintenanceResponse(){
        SellerLoginErrorResponse response=new SellerLoginErrorResponse();
        response.setError("Login service is currently unavailable");
        response.setServiceAvailable(false);
        return response;
    }
}