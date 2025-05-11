package com.dishes.controllers;

import com.dishes.dto.AuthResponse;
import com.dishes.entities.Customer;
import com.dishes.services.AuthService;
import com.dishes.services.CustomerService;

import jakarta.annotation.security.RolesAllowed;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerCustomer(@RequestBody Customer customer) {
        var validationError = customerService.validate(customer);
        if (validationError.isPresent()) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(false, validationError.get(), null, null));
        }

        Customer savedCustomer = customerService.register(customer);
        String token = authService.generateJwtToken(savedCustomer);
        
        return ResponseEntity.ok(
            new AuthResponse(
                true, 
                "Registered successfully!", 
                token,
                3600L
            )
        );
    }

    @GetMapping("/getCustomers")
    @RolesAllowed("ADMIN")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }
}