package com.dishes.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import com.dishes.clients.fallback.AdminServiceClientFallback;
import com.dishes.dtos.SellerLoginRequest;

@FeignClient(name = "admin-service", url = "${admin.service.url}", fallback = AdminServiceClientFallback.class)
public interface AdminServiceClient {
    @PostMapping("/admin-services/api/admin/seller/login")
    ResponseEntity<?> authenticateSeller(SellerLoginRequest req);
}
