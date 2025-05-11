package com.dishes.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import com.dishes.clients.fallback.AdminServiceClientFallback;
import com.dishes.dtos.SellerLoginRequest;
import com.dishes.interfaces.SellerLoginResponse;

@FeignClient(name = "admin-service", url = "${admin.service.url}", fallback = AdminServiceClientFallback.class)
public interface AdminServiceClient {
    @PostMapping("/admin-services/api/admin/seller/login")
    SellerLoginResponse authenticateSeller(SellerLoginRequest req);
}
