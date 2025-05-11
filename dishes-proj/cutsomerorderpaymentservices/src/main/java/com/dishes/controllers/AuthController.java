package com.dishes.controllers;

import com.dishes.dto.AuthResponse;
import com.dishes.dto.LoginDTO;
import com.dishes.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO request) {
        AuthResponse response = authService.login(request.getEmail(), request.getPassword());
        if (response.isSuccess()) {
            return ResponseEntity.ok().body(response);
        }
        else{
            return ResponseEntity.badRequest().body(response);
        }
    }
}