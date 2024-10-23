package com.spkt.librasys.controller;

import com.spkt.librasys.dto.response.ApiResponse;
import com.spkt.librasys.dto.response.dashboard.DashboardDocumentCountResponse;
import com.spkt.librasys.dto.response.dashboard.DashboardLoanTransactionCountResponse;
import com.spkt.librasys.dto.response.dashboard.DashboardTopBorrowedDocumentsResponse;
import com.spkt.librasys.service.DashboardService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    DashboardService dashboardService;

    // Lấy tổng số tài liệu
    @GetMapping("/documents-count")
    public ApiResponse<DashboardDocumentCountResponse> getDocumentCount() {
        DashboardDocumentCountResponse response = dashboardService.getDocumentCount();
        return ApiResponse.<DashboardDocumentCountResponse>builder()
                .message("Document count retrieved successfully")
                .result(response)
                .build();
    }

    // Lấy số lượng giao dịch mượn sách theo tháng
    @GetMapping("/loan-transactions-count")
    public ApiResponse<DashboardLoanTransactionCountResponse> getLoanTransactionsCount() {
        DashboardLoanTransactionCountResponse response = dashboardService.getLoanTransactionsCount();
        return ApiResponse.<DashboardLoanTransactionCountResponse>builder()
                .message("Loan transaction count retrieved successfully")
                .result(response)
                .build();
    }

    // Lấy danh sách tài liệu được mượn nhiều nhất
    @GetMapping("/top-borrowed-documents")
    public ApiResponse<DashboardTopBorrowedDocumentsResponse> getTopBorrowedDocuments() {
        DashboardTopBorrowedDocumentsResponse response = dashboardService.getTopBorrowedDocuments();
        return ApiResponse.<DashboardTopBorrowedDocumentsResponse>builder()
                .message("Top borrowed documents retrieved successfully")
                .result(response)
                .build();
    }

    @GetMapping("/new-users-count")
    public ApiResponse<Long> getNewUsersCount() {
        Long count = dashboardService.getNewUsersCount();
        return ApiResponse.<Long>builder()
                .message("New users count retrieved successfully")
                .result(count)
                .build();
    }

    @GetMapping("/unpaid-fines-count")
    public ApiResponse<Long> getUnpaidFinesCount() {
        Long count = dashboardService.getUnpaidFinesCount();
        return ApiResponse.<Long>builder()
                .message("Unpaid fines count retrieved successfully")
                .result(count)
                .build();
    }

    @GetMapping("/unreturned-documents-count")
    public ApiResponse<Long> getUnreturnedDocumentsCount() {
        Long count = dashboardService.getUnreturnedDocumentsCount();
        return ApiResponse.<Long>builder()
                .message("Unreturned documents count retrieved successfully")
                .result(count)
                .build();
    }

    @GetMapping("/monthly-active-users-count")
    public ApiResponse<Long> getMonthlyActiveUsersCount() {
        Long count = dashboardService.getMonthlyActiveUsersCount();
        return ApiResponse.<Long>builder()
                .message("Monthly active users count retrieved successfully")
                .result(count)
                .build();
    }


}