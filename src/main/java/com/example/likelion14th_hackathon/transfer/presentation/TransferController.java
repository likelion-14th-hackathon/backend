package com.example.likelion14th_hackathon.transfer.presentation;

import com.example.likelion14th_hackathon.common.response.ApiResponse;
import com.example.likelion14th_hackathon.transfer.application.TransferService;
import com.example.likelion14th_hackathon.transfer.domain.Transfer;
import com.example.likelion14th_hackathon.transfer.domain.TransferStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping({"/transfers", "/api/transfers"})
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/owned-products/{ownedProductId}")
    public ApiResponse<TransferResponse> requestTransfer(
            @PathVariable Long ownedProductId,
            @Valid @RequestBody TransferRequest request
    ) {
        return ApiResponse.success(TransferResponse.from(transferService.requestTransfer(ownedProductId, request.toEmail())));
    }

    @PostMapping("/{transferId}/complete")
    public ApiResponse<TransferResponse> completeTransfer(@PathVariable Long transferId) {
        return ApiResponse.success(TransferResponse.from(transferService.completeTransfer(transferId)));
    }

    public record TransferRequest(
            @Email(message = "양도받을 사람의 이메일 형식이 올바르지 않습니다.")
            @NotBlank(message = "양도받을 사람의 이메일은 필수입니다.")
            String toEmail
    ) {
    }

    public record TransferResponse(
            Long transferId,
            Long ownedProductId,
            String fromEmail,
            String toEmail,
            TransferStatus status,
            LocalDateTime requestedAt,
            LocalDateTime completedAt
    ) {
        public static TransferResponse from(Transfer transfer) {
            return new TransferResponse(
                    transfer.getId(),
                    transfer.getOwnedProduct().getId(),
                    transfer.getFromMember().getEmail(),
                    transfer.getToEmail(),
                    transfer.getStatus(),
                    transfer.getRequestedAt(),
                    transfer.getCompletedAt()
            );
        }
    }
}

