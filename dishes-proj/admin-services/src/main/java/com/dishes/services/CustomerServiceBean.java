package com.dishes.services;

import java.util.List;

import com.dishes.clients.CustomerServiceClient;
import com.dishes.dtos.UserDTO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
@ApplicationScoped
public class CustomerServiceBean {
    @Inject
    CustomerServiceClient customerServiceClient;

    public List<UserDTO> getAllCustomers(){
        return customerServiceClient.getAllCustomers();
    }
}