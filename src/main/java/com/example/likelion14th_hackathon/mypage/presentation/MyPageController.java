package com.example.likelion14th_hackathon.mypage.presentation;

import com.example.likelion14th_hackathon.common.response.ApiResponse;
import com.example.likelion14th_hackathon.common.security.JwtTokenProvider;
import com.example.likelion14th_hackathon.mypage.application.MyPageService;
import com.example.likelion14th_hackathon.mypage.domain.OwnedProduct;
import com.example.likelion14th_hackathon.mypage.presentation.dto.OwnedProductResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/mypage", "/api/mypage"})
public class MyPageController {

    private final MyPageService myPageService;
    private final JwtTokenProvider jwtTokenProvider;

    public MyPageController(MyPageService myPageService, JwtTokenProvider jwtTokenProvider) {
        this.myPageService = myPageService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/owned-products")
    public ApiResponse<List<OwnedProductResponse>> getMyOwnedProducts(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        Long memberId = jwtTokenProvider.extractMemberIdFromAuthorization(authorizationHeader);
        return ApiResponse.success(toResponse(myPageService.getOwnedProducts(memberId)));
    }

    @GetMapping("/members/{memberId}/owned-products")
    public ApiResponse<List<OwnedProductResponse>> getOwnedProducts(@PathVariable Long memberId) {
        return ApiResponse.success(toResponse(myPageService.getOwnedProducts(memberId)));
    }

    private List<OwnedProductResponse> toResponse(List<OwnedProduct> ownedProducts) {
        return ownedProducts.stream()
                .map(OwnedProductResponse::from)
                .toList();
    }
}
