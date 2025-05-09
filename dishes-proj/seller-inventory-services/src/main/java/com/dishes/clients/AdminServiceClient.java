package com.dishes.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import com.dishes.dtos.SellerLoginRequest;
import com.dishes.dtos.SellerLoginResponse;

@FeignClient(name = "admin-service", url = "${admin.service.url}")
public interface AdminServiceClient {
    @PostMapping("/admin-services/api/admin/seller/login")
    SellerLoginResponse authenticateSeller(SellerLoginRequest req);
}
