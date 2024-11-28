package com.spkt.librasys.controller;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.request.BarcodeScanRequest;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Lớp Controller xử lý các yêu cầu giao dịch mượn sách.
 * Cung cấp các endpoint để tạo, cập nhật, truy xuất và quản lý các giao dịch mượn sách.
 */
@RestController
@RequestMapping("/api/v1/loan-transactions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LoanTransactionController {

    LoanTransactionService loanTransactionService;

    /**
     * Tạo một yêu cầu mượn sách mới.
     * Endpoint này cho phép người dùng tạo yêu cầu mượn sách.
     *
     * @param request Thông tin yêu cầu mượn sách.
     * @return ApiResponse chứa thông tin của giao dịch mượn sách vừa tạo.
     */
    @PostMapping
    public ApiResponse<LoanTransactionResponse> createLoanTransaction(@RequestBody LoanTransactionRequest request) {
        LoanTransactionResponse response = loanTransactionService.createLoanTransaction(request);
        return ApiResponse.<LoanTransactionResponse>builder()
                .message("Yêu cầu mượn sách đã được tạo thành công")
                .result(response)
                .build();
    }

    /**
     * Cập nhật một giao dịch mượn sách đã có dựa trên hành động cụ thể.
     * Các hành động bao gồm nhận sách, trả sách, hủy giao dịch, phê duyệt yêu cầu, từ chối yêu cầu...
     *
     * @param request Thông tin cập nhật giao dịch.
     * @return ApiResponse chứa thông tin của giao dịch mượn sách đã được cập nhật.
     */
    @PatchMapping
    public ApiResponse<LoanTransactionResponse> updateTransaction(@Valid @RequestBody LoanTransactionUpdateRequest request) {
        LoanTransactionResponse response;
        String message = switch (request.getAction()) {
            case RECEIVE -> {
                response = loanTransactionService.receiveDocument(request.getTransactionId(), true);
                yield "Người dùng đã nhận sách thành công";
            }
            case RETURN_REQUEST -> {
                response = loanTransactionService.returnDocument(request.getTransactionId(), true);
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

    /**
     * Xác nhận việc trả sách sau khi sách đã được trả.
     * Endpoint này dùng để xác nhận rằng một cuốn sách đã được trả lại.
     *
     * @param request Thông tin yêu cầu xác nhận trả sách.
     * @return ApiResponse chứa thông tin của giao dịch mượn sách đã được cập nhật.
     */
    @PatchMapping("/confirm-return")
    public ApiResponse<LoanTransactionResponse> confirmReturnDocument(@Valid @RequestBody LoanTransactionReturnRequest request) {
        LoanTransactionResponse response = loanTransactionService.confirmReturnDocument(request);
        return ApiResponse.<LoanTransactionResponse>builder()
                .message("Xác nhận trả sách thành công")
                .result(response)
                .build();
    }

    /**
     * Truy xuất thông tin chi tiết của một giao dịch mượn sách cụ thể bằng ID.
     * Endpoint này cho phép người dùng xem chi tiết giao dịch mượn sách.
     *
     * @param id ID của giao dịch mượn sách.
     * @return ApiResponse chứa thông tin chi tiết giao dịch mượn sách.
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
     * Truy xuất danh sách tất cả các giao dịch mượn sách với các tùy chọn tìm kiếm nâng cao và phân trang.
     * Endpoint này cho phép người dùng lấy danh sách các giao dịch mượn sách với các bộ lọc và phân trang.
     *
     * @param request Các bộ lọc tìm kiếm giao dịch mượn sách.
     * @param pageable Thông tin phân trang (số trang, kích thước trang, hướng sắp xếp).
     * @return ApiResponse chứa danh sách các giao dịch mượn sách theo phân trang.
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
     * Tự động hủy các giao dịch mượn sách đã được phê duyệt nhưng chưa nhận trong vòng 24 giờ.
     * Endpoint này kiểm tra và hủy các giao dịch quá hạn đã phê duyệt mà chưa được nhận.
     *
     * @return ApiResponse thông báo rằng các giao dịch quá hạn đã bị hủy.
     */
    @PatchMapping("/cancel-expired")
    public ApiResponse<Void> cancelExpiredTransactions() {
        loanTransactionService.cancelExpiredApprovedTransactions();
        return ApiResponse.<Void>builder()
                .message("Đã kiểm tra và huỷ các yêu cầu mượn sách quá hạn 24 giờ mà chưa nhận sách")
                .build();
    }

    /**
     * Kiểm tra xem người dùng có đang mượn cuốn sách cụ thể nào không.
     * Endpoint này cho phép người dùng kiểm tra xem họ có mượn một cuốn sách cụ thể hay không.
     *
     * @param documentId ID của cuốn sách cần kiểm tra.
     * @return ApiResponse chứa thông tin boolean cho biết người dùng có đang mượn cuốn sách này hay không.
     */
    @GetMapping("/user/check-user-borrowing/{documentId}")
    public ApiResponse<Boolean> checkUserBorrowingDocument(@PathVariable Long documentId) {
        boolean isBorrowing = loanTransactionService.isUserBorrowingDocument(documentId);
        return ApiResponse.<Boolean>builder()
                .message(isBorrowing ? "Người dùng đang mượn cuốn sách này" : "Người dùng không mượn cuốn sách này")
                .result(isBorrowing)
                .build();
    }

    /**
     * Truy xuất danh sách các sách hiện tại mà người dùng đang mượn.
     * Endpoint này cung cấp danh sách các cuốn sách mà người dùng đang mượn.
     *
     * @param pageable Thông tin phân trang (số trang, kích thước trang, hướng sắp xếp).
     * @return ApiResponse chứa danh sách các sách người dùng đang mượn theo phân trang.
     */
    @GetMapping("/user/borrowed-books")
    public ApiResponse<PageDTO<LoanTransactionResponse>> getUserBorrowedBooks(Pageable pageable) {
        Page<LoanTransactionResponse> borrowedBooks = loanTransactionService.getUserBorrowedBooks(pageable);
        PageDTO<LoanTransactionResponse> response = new PageDTO<>(borrowedBooks);
        return ApiResponse.<PageDTO<LoanTransactionResponse>>builder()
                .message("Danh sách các sách đang mượn của người dùng")
                .result(response)
                .build();
    }

    /**
     * Xử lý quét mã vạch của một tài liệu (sách) trong giao dịch mượn sách.
     * Endpoint này xử lý dữ liệu từ mã vạch được quét và cập nhật giao dịch mượn sách tương ứng.
     *
     * @param request Yêu cầu quét mã vạch chứa dữ liệu mã vạch.
     * @return ApiResponse chứa thông tin của giao dịch mượn sách đã được cập nhật.
     */
    @PostMapping("/scan-qrcode")
    public ApiResponse<LoanTransactionResponse> handleBarcodeScan(@RequestBody @Valid BarcodeScanRequest request) {
        LoanTransactionResponse response = loanTransactionService.handleQrcodeScan(request.getBarcodeData());
        return ApiResponse.<LoanTransactionResponse>builder()
                .message("Giao dịch được cập nhật thành công dựa trên quét mã vạch")
                .result(response)
                .build();
    }

    /**
     * Truy xuất hình ảnh mã QR của một giao dịch mượn sách.
     * Endpoint này tạo ra hình ảnh mã QR cho giao dịch mượn sách cụ thể.
     *
     * @param transactionId ID của giao dịch mượn sách.
     * @return ResponseEntity chứa hình ảnh mã QR.
     */
    @GetMapping("/{transactionId}/qrcode-image")
    public ResponseEntity<byte[]> getBarcodeImage(@PathVariable Long transactionId) {
        byte[] qrCodeImage = loanTransactionService.getQrcodeImage(transactionId);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(qrCodeImage);
    }
}
