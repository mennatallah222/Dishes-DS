package com.dishes.controllers;

import javax.naming.ServiceUnavailableException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dishes.dto.AddOrderDTO;
import com.dishes.dto.ErrorResponse;
import com.dishes.dto.OrderResponse;
import com.dishes.services.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService=orderService;
    }

    @RequestMapping("/add-order")
    public ResponseEntity<?> addOrder( @RequestHeader("Authorization") String authHeader, @RequestBody AddOrderDTO request) throws ServiceUnavailableException{
        OrderResponse response = orderService.addOrder(request, authHeader);
        if("FAILED".equals(response.getStatus())){
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ErrorResponse(response.getErrorMessage(), response.getErrorCode()));
        }
        return ResponseEntity.ok(response);
    }
}