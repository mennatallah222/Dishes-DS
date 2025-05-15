package com.dishes.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderFailedEvent {
    private Long orderId;
    private Long customerId;
    private double amount;
    private String reason;
    private LocalDateTime timestamp;
}