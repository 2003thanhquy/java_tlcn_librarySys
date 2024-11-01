package com.spkt.librasys.controller;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.request.loanTransaction.*;
import com.spkt.librasys.dto.response.ApiResponse;
import com.spkt.librasys.dto.response.LoanTransactionResponse;
import com.spkt.librasys.exception.AppException;
import com.spkt.librasys.exception.ErrorCode;
import com.spkt.librasys.service.LoanTransactionService;
import jakarta.validation.Valid;
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

    /**
     * Tạo yêu cầu mượn sách (POST)
     */
    @PostMapping
    public ApiResponse<LoanTransactionResponse> createLoanTransaction(@RequestBody LoanTransactionRequest request) {
        LoanTransactionResponse response = loanTransactionService.createLoanTransaction(request);
        return ApiResponse.<LoanTransactionResponse>builder()
                .message("Yêu cầu mượn sách đã được tạo thành công")
                .result(response)
                .build();
    }

    @PatchMapping
    public ApiResponse<LoanTransactionResponse> updateTransaction(@Valid @RequestBody LoanTransactionUpdateRequest request) {
        LoanTransactionResponse response;
        String message = switch (request.getAction()) {
            case RECEIVE -> {
                response = loanTransactionService.receiveDocument(request.getTransactionId());
                yield "Người dùng đã nhận sách thành công";
            }
            case RETURN_REQUEST -> {
                response = loanTransactionService.returnDocument(request.getTransactionId());
                yield "Sách đã được trả thành công";
            }
            case CANCEL -> {
                response = loanTransactionService.cancelLoanTransactionByUser(request.getTransactionId());
                yield "Yêu cầu mượn sách đã được người dùng hủy thành công";
            }
            case APPROVE -> {
                response = loanTransactionService.approveTransaction(request.getTransactionId());
                yield "Yêu cầu mượn sách đã được phê duyệt";
            }
            case REJECTED -> {
                response = loanTransactionService.rejectTransaction(request.getTransactionId());
                yield "Yêu cầu mượn sách đã bị từ chối";
            }
            default -> throw new AppException(ErrorCode.INVALID_REQUEST, "Hành động không hợp lệ");
        };

        return ApiResponse.<LoanTransactionResponse>builder()
                .message(message)
                .result(response)
                .build();
    }
    @PatchMapping("/confirm-return")
    public ApiResponse<LoanTransactionResponse> confirmReturnDocument(@Valid@RequestBody LoanTransactionReturnRequest request) {
        LoanTransactionResponse response = loanTransactionService.confirmReturnDocument(request);
        return ApiResponse.<LoanTransactionResponse>builder()
                .message("Xác nhận trả sách thành công")
                .result(response)
                .build();
    }
    /**
     * Lấy thông tin chi tiết của giao dịch mượn sách (GET)
     */
    @GetMapping("/{id}")
    public ApiResponse<LoanTransactionResponse> getLoanTransactionById(@PathVariable Long id) {
        LoanTransactionResponse response = loanTransactionService.getLoanTransactionById(id);
        return ApiResponse.<LoanTransactionResponse>builder()
                .message("Thông tin giao dịch được lấy thành công")
                .result(response)
                .build();
    }

    /**
     * Lấy danh sách tất cả các giao dịch mượn sách với tìm kiếm nâng cao và phân trang (GET)
     */
    @GetMapping
    public ApiResponse<PageDTO<LoanTransactionResponse>> getAllLoanTransactions(
            LoanTransactionSearchRequest request,
            Pageable pageable) {
        Page<LoanTransactionResponse> response = loanTransactionService.getAllLoanTransactions(request, pageable);
        PageDTO<LoanTransactionResponse> pageDTO = new PageDTO<>(response);
        return ApiResponse.<PageDTO<LoanTransactionResponse>>builder()
                .message("Tất cả giao dịch được lấy thành công")
                .result(pageDTO)
                .build();
    }

    /**
     * Tự động hủy các yêu cầu đã được phê duyệt nhưng chưa nhận sách trong vòng 24 giờ (Scheduled Task) (PATCH)
     */
    @PatchMapping("/cancel-expired")
    public ApiResponse<Void> cancelExpiredTransactions() {
        loanTransactionService.cancelExpiredApprovedTransactions();
        return ApiResponse.<Void>builder()
                .message("Đã kiểm tra và huỷ các yêu cầu mượn sách quá hạn 24 giờ mà chưa nhận sách")
                .build();
    }
    @GetMapping("/user/check-user-borrowing/{documentId}")
    public ApiResponse<Boolean> checkUserBorrowingDocument(@PathVariable Long documentId) {
        boolean isBorrowing = loanTransactionService.isUserBorrowingDocument( documentId);
        return ApiResponse.<Boolean>builder()
                .message(isBorrowing ? "Người dùng đang mượn cuốn sách này" : "Người dùng không mượn cuốn sách này")
                .result(isBorrowing)
                .build();
    }
    @GetMapping("/user/borrowed-books")
    public ApiResponse<PageDTO<LoanTransactionResponse>> getUserBorrowedBooks(Pageable pageable) {
        Page<LoanTransactionResponse> borrowedBooks = loanTransactionService.getUserBorrowedBooks(pageable);
        PageDTO<LoanTransactionResponse> response = new PageDTO<>(borrowedBooks);
        return ApiResponse.<PageDTO<LoanTransactionResponse>>builder()
                .message("Danh sách các sách đang mượn của người dùng")
                .result(response)
                .build();
    }
}
