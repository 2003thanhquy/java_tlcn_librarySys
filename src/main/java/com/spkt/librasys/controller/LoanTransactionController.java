package com.spkt.librasys.controller;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.request.loanTransactionRequest.LoanTransactionRequest;
import com.spkt.librasys.dto.request.loanTransactionRequest.UpdateTransactionStatusRequest;
import com.spkt.librasys.dto.response.ApiResponse;
import com.spkt.librasys.dto.response.LoanTransactionResponse;
import com.spkt.librasys.service.LoanTransactionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/loan-transactions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LoanTransactionController {

    LoanTransactionService loanTransactionService;

    @PostMapping
    public ApiResponse<LoanTransactionResponse> createLoanTransaction(@RequestBody LoanTransactionRequest request) {
        LoanTransactionResponse response = loanTransactionService.createLoanTransaction(request);
        return ApiResponse.<LoanTransactionResponse>builder()
                .message("Yêu cầu mượn sách đã được tạo thành công")
                .result(response)
                .build();
    }

    @PutMapping("/{id}/approve")
    public ApiResponse<LoanTransactionResponse> approveTransaction(@PathVariable Long id,@RequestBody UpdateTransactionStatusRequest request) {
       boolean isApproved = request.getIsApproved();
        LoanTransactionResponse response = loanTransactionService.approveTransaction(id, isApproved);
        return ApiResponse.<LoanTransactionResponse>builder()
                .message(isApproved ? "Yêu cầu mượn sách đã được phê duyệt" : "Yêu cầu mượn sách đã bị từ chối")
                .result(response)
                .build();
    }

    @PutMapping("/{id}/receive")
    public ApiResponse<LoanTransactionResponse> receiveDocument(@PathVariable Long id) {
        LoanTransactionResponse response = loanTransactionService.receiveDocument(id);
        return ApiResponse.<LoanTransactionResponse>builder()
                .message("Người dùng đã nhận sách thành công")
                .result(response)
                .build();
    }


    @PutMapping("/{id}/return")
    public ApiResponse<LoanTransactionResponse> returnDocument(@PathVariable Long id) {
        LoanTransactionResponse response = loanTransactionService.returnDocument(id);
        return ApiResponse.<LoanTransactionResponse>builder()
                .message("Sách đã được trả thành công")
                .result(response)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<LoanTransactionResponse> getLoanTransactionById(@PathVariable Long id) {
        LoanTransactionResponse response = loanTransactionService.getLoanTransactionById(id);
        return ApiResponse.<LoanTransactionResponse>builder()
                .message("Thông tin giao dịch được lấy thành công")
                .result(response)
                .build();
    }

    @GetMapping
    public ApiResponse<PageDTO<LoanTransactionResponse>> getAllLoanTransactions(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String documentName,
            Pageable pageable) {

        Page<LoanTransactionResponse> response = loanTransactionService.getAllLoanTransactions(status, username, documentName, pageable);
        PageDTO<LoanTransactionResponse> pageDTO = new PageDTO<>(response);
        return ApiResponse.<PageDTO<LoanTransactionResponse>>builder()
                .message("Tất cả giao dịch được lấy thành công")
                .result(pageDTO)
                .build();
    }

    // Phương thức để huỷ yêu cầu mượn sách
    @PutMapping("/{id}/cancel")
    public ApiResponse<LoanTransactionResponse> cancelLoanTransactionByUser(@PathVariable Long id) {
        LoanTransactionResponse response = loanTransactionService.cancelLoanTransactionByUser(id);
        return ApiResponse.<LoanTransactionResponse>builder()
                .message("Yêu cầu mượn sách đã được người dùng hủy thành công")
                .result(response)
                .build();
    }

    // Phương thức để kiểm tra và tự động huỷ các yêu cầu đã được duyệt nhưng không nhận trong vòng 24 giờ
    @PutMapping("/cancel-expired")
    public ApiResponse<Void> cancelExpiredTransactions() {
        loanTransactionService.cancelExpiredApprovedTransactions();
        return ApiResponse.<Void>builder()
                .message("Đã kiểm tra và huỷ các yêu cầu mượn sách quá hạn 24 giờ mà chưa nhận sách")
                .build();
    }
}
