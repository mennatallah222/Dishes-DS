package com.dishes.services;

import com.dishes.entities.Customer;
import com.dishes.repositories.CustomerRepository;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpSession;

@Service
public class AuthService {

    @Autowired
    private CustomerRepository userRepository;

    public boolean login(String email, String password, HttpSession session) {
        Optional<Customer> user = userRepository.findByEmailAndPassword(email, password);
        if (user != null) {
            session.setAttribute("currentUser", user); // store in session
            return true;
        }
        return false;
    }

    public Customer getCurrentUser(HttpSession session) {
        return (Customer) session.getAttribute("currentUser");
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }
}
