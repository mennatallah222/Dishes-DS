package com.dishes.controllers;

import java.util.concurrent.CompletableFuture;

import javax.naming.ServiceUnavailableException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dishes.dtos.AddOrderDTO;
import com.dishes.dtos.OrderResponse;
import com.dishes.services.OrderService;

import jakarta.annotation.security.RolesAllowed;

@RestController
@RequestMapping("/orders")
@RolesAllowed("CUSTOMER")
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService=orderService;
    }

    @RequestMapping("/add-order")
    public CompletableFuture<ResponseEntity<OrderResponse>> addOrder( @RequestHeader("Authorization") String authHeader, @RequestBody AddOrderDTO request) throws ServiceUnavailableException{
        return orderService.addOrder(request, authHeader).thenApply(response->{
            if("COMPLETED".equals(response.getStatus())){
                return ResponseEntity.ok(response);
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        
        });
    }

    @PostMapping("/checkout/{orderId}")
    public ResponseEntity<OrderResponse> checkoutOrder(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("orderId") Long orderId) throws ServiceUnavailableException {
        OrderResponse or = orderService.checkoutOrder(orderId);
        if ("CONFIRMED".equals(or.getStatus())){
            return ResponseEntity.ok(or);
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(or);
        }
    }


}