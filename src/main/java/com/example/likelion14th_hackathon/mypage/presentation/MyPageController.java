package com.example.likelion14th_hackathon.mypage.presentation;

import com.example.likelion14th_hackathon.common.response.ApiResponse;
import com.example.likelion14th_hackathon.mypage.application.MyPageService;
import com.example.likelion14th_hackathon.mypage.domain.OwnedProduct;
import com.example.likelion14th_hackathon.mypage.presentation.dto.OwnedProductResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/mypage", "/api/mypage"})
public class MyPageController {

    private final MyPageService myPageService;

    public MyPageController(MyPageService myPageService) {
        this.myPageService = myPageService;
    }

    @GetMapping("/members/{memberId}/owned-products")
    public ApiResponse<List<OwnedProductResponse>> getOwnedProducts(@PathVariable Long memberId) {
        List<OwnedProduct> ownedProducts = myPageService.getOwnedProducts(memberId);
        List<OwnedProductResponse> response = ownedProducts.stream()
                .map(OwnedProductResponse::from)
                .toList();

        return ApiResponse.success(response);
    }
}
