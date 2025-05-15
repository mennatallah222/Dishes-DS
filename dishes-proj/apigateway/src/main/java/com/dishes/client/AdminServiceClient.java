package com.dishes.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.dishes.dto.OrderFailedEvent;

@FeignClient(name = "admin-service", url = "${admin.service.url}")
public interface AdminServiceClient {
    
    @PostMapping("/api/admin/order-failures")
    void notifyAdmins(@RequestBody OrderFailedEvent event);
}