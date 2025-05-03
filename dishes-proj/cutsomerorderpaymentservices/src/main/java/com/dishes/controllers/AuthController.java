package com.dishes.controllers;

import com.dishes.dto.LoginDTO;
import com.dishes.services.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public String login(@RequestBody LoginDTO request, HttpSession session) {
        if (authService.login(request.getEmail(), request.getPassword(), session)) {
            return "Login successful";
        } else {
            return "Invalid credentials";
        }
    }


    @PostMapping("/logout")
    public String logout(HttpSession session) {
        authService.logout(session);
        return "Logged out";
    }

    @GetMapping("/me")
    public Object currentUser(HttpSession session) {
        return authService.getCurrentUser(session);
    }
}
