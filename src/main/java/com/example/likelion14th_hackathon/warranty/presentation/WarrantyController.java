package com.example.likelion14th_hackathon.warranty.presentation;

import com.example.likelion14th_hackathon.common.response.ApiResponse;
import com.example.likelion14th_hackathon.warranty.application.WarrantyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/warranties")
public class WarrantyController {

    private final WarrantyService warrantyService;

    public WarrantyController(WarrantyService warrantyService) {
        this.warrantyService = warrantyService;
    }

    @GetMapping("/owned-products/{ownedProductId}")
    public ApiResponse<WarrantyService.WarrantyResponse> getWarranty(@PathVariable Long ownedProductId) {
        return ApiResponse.success(warrantyService.getMockWarranty(ownedProductId));
    }
}
