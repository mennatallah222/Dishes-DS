package com.dishes.controllers;

import com.dishes.entities.Customer;
import com.dishes.services.CustomerService;

import jakarta.annotation.security.RolesAllowed;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<?> registerCustomer(@RequestBody Customer customer) {
        var validationError = customerService.validate(customer);
        if (validationError.isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new Response(false, validationError.get()));
        }

        Customer savedCustomer = customerService.register(customer);
        return ResponseEntity.ok(new Response(true, "Customer registered successfully", savedCustomer));
    }

    @GetMapping("/getCustomers")
    @RolesAllowed("ADMIN")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }
}
