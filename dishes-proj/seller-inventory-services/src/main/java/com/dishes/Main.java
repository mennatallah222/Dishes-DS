package com.dishes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableFeignClients
@EnableTransactionManagement
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
