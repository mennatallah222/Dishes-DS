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
public class SellerAuth {
    private final AdminServiceClient adminServiceClient;
    private final CircuitBreaker circuitBreaker;
    private final LoggingService loggingService;

    public SellerAuth(AdminServiceClient adminServiceClient, 
                    CircuitBreakerRegistry circuitBreakerRegistry, 
                    LoggingService loggingService) {
        this.adminServiceClient = adminServiceClient;
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("adminService");
        this.loggingService = loggingService;
    }

    public SellerLoginResponse authenticateSeller(SellerLoginRequest req) {
        loggingService.logInfo("Attempting seller authentication for email: " + req.getEmail());
        
        if(circuitBreaker.getState() == CircuitBreaker.State.OPEN) {
            loggingService.logError("Circuit breaker is OPEN - admin service unavailable");
            return createMaintenanceResponse();
        }
        
        try {
            ResponseEntity<?> response = adminServiceClient.authenticateSeller(req);
            
            if(response.getStatusCode().is2xxSuccessful()) {
                Object body = response.getBody();
                if(body instanceof Map<?, ?> mb) {
                    SellerLoginSuccessResponse success = new SellerLoginSuccessResponse();
                    success.setToken((String)mb.get("token"));
                    loggingService.logInfo("Seller authentication successful for email: " + req.getEmail());
                    return success;
                }
            } else {
                Object body = response.getBody();
                if(body instanceof Map<?, ?> mb) {
                    SellerLoginErrorResponse error = new SellerLoginErrorResponse();
                    error.setError((String)mb.get("error"));
                    error.setServiceAvailable(false);
                    loggingService.logWarning("Seller authentication failed for email: "+req.getEmail()+" - " + mb.get("error"));
                    return error;
                }
            }
        }
        catch(Exception e) {
            loggingService.logError("Exception during seller authentication: " + e.getMessage());
            e.printStackTrace();
            return createMaintenanceResponse();
        }
        
        loggingService.logWarning("Unexpected authentication response format for email: " + req.getEmail());
        return createMaintenanceResponse();
    }

    private SellerLoginErrorResponse createMaintenanceResponse() {
        SellerLoginErrorResponse response = new SellerLoginErrorResponse();
        response.setError("Login service is currently unavailable");
        response.setServiceAvailable(false);
        return response;
    }
}