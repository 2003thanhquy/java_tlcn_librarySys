package com.spkt.librasys.controller;

import com.spkt.librasys.dto.response.ApiResponse;
import com.spkt.librasys.dto.response.dashboard.*;
import com.spkt.librasys.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dashboards")
@Slf4j
public class DashboardController {

    private final DashboardService dashboardService;

    // Lấy tổng số tài liệu
    @GetMapping("/documents/count")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ApiResponse<DashboardDocumentCountResponse> getDocumentCount() {
        log.info("Getting document count");
        DashboardDocumentCountResponse response = dashboardService.getDocumentCount();
        return ApiResponse.<DashboardDocumentCountResponse>builder()
                .message("Document count retrieved successfully")
                .result(response)
                .build();
    }

    // Lấy số lượng giao dịch mượn sách theo tháng, chọn tháng và năm
    @GetMapping("/loans/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<DashboardLoanTransactionCountResponse> getLoanTransactionsCount(
            @RequestParam int month,
            @RequestParam int year) {
        log.info("Getting loan transactions count for month: {} and year: {}", month, year);
        DashboardLoanTransactionCountResponse response = dashboardService.getLoanTransactionsCount(month, year);
        return ApiResponse.<DashboardLoanTransactionCountResponse>builder()
                .message("Loan transaction count retrieved successfully")
                .result(response)
                .build();
    }

    // Lấy danh sách tài liệu được mượn nhiều nhất, hỗ trợ phân trang
    @GetMapping("/documents/top-borrowed")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ApiResponse<Page<DashboardTopBorrowedDocumentsResponse>> getTopBorrowedDocuments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting top borrowed documents for page: {} and size: {}", page, size);
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<DashboardTopBorrowedDocumentsResponse> response = dashboardService.getTopBorrowedDocuments(pageRequest);
        return ApiResponse.<Page<DashboardTopBorrowedDocumentsResponse>>builder()
                .message("Top borrowed documents retrieved successfully")
                .result(response)
                .build();
    }

    // Lấy số lượng người dùng mới
    @GetMapping("/users/new/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Long> getNewUsersCount(@RequestParam int month, @RequestParam int year) {
        log.info("Getting new users count for month: {} and year: {}", month, year);
        Long count = dashboardService.getNewUsersCount(month, year);
        return ApiResponse.<Long>builder()
                .message("New users count retrieved successfully")
                .result(count)
                .build();
    }

    // Lấy số lượng khoản phạt chưa thanh toán
    @GetMapping("/fines/unpaid/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Long> getUnpaidFinesCount() {
        log.info("Getting unpaid fines count");
        Long count = dashboardService.getUnpaidFinesCount();
        return ApiResponse.<Long>builder()
                .message("Unpaid fines count retrieved successfully")
                .result(count)
                .build();
    }

    // Lấy số lượng tài liệu chưa trả
    @GetMapping("/documents/unreturned/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Long> getUnreturnedDocumentsCount() {
        log.info("Getting unreturned documents count");
        Long count = dashboardService.getUnreturnedDocumentsCount();
        return ApiResponse.<Long>builder()
                .message("Unreturned documents count retrieved successfully")
                .result(count)
                .build();
    }

    // Lấy số lượng người dùng hoạt động hàng tháng
    @GetMapping("/users/active/monthly/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Long> getMonthlyActiveUsersCount(@RequestParam int month, @RequestParam int year) {
        log.info("Getting monthly active users count for month: {} and year: {}", month, year);
        Long count = dashboardService.getMonthlyActiveUsersCount(month, year);
        return ApiResponse.<Long>builder()
                .message("Monthly active users count retrieved successfully")
                .result(count)
                .build();
    }
    // API: Thống kê người dùng tổng hợp
    @GetMapping("/users/statistics")
    public ApiResponse<UserStatisticsResponse> getUserStatistics(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {

        // Lấy dữ liệu thống kê từ service
        UserStatisticsResponse response = dashboardService.getUserStatistics(month, year);

        return ApiResponse.<UserStatisticsResponse>builder()
                .message("User statistics retrieved successfully")
                .result(response)
                .build();
    }
    // API: Thống kê tài liệu
    @GetMapping("/documents/statistics")
    public ApiResponse<DocumentStatisticsResponse> getDocumentStatistics(
            @RequestParam(defaultValue = "#{T(java.time.Year).now().toString()}") String year) {

        DocumentStatisticsResponse response = dashboardService.getDocumentStatistics(year);

        return ApiResponse.<DocumentStatisticsResponse>builder()
                .message("Document statistics retrieved successfully")
                .result(response)
                .build();
    }
    // API: Thống kê mượn trả tổng hợp
    @GetMapping("/loans/statistics")
    public ApiResponse<LoanTransactionStatisticsResponse> getLoanTransactionStatistics(
            @RequestParam(defaultValue = "#{T(java.time.Year).now().value}") int year) {

        LoanTransactionStatisticsResponse response = dashboardService.getLoanTransactionStatistics(year);

        return ApiResponse.<LoanTransactionStatisticsResponse>builder()
                .message("Loan transaction statistics retrieved successfully")
                .result(response)
                .build();
    }

    // API: Bảng hoạt động mượn trả theo startDate -> endDate
    @GetMapping("/loans/activities")
    public ApiResponse<Map<String, Object>> getLoanTransactionActivities(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        Map<String, Object> response = dashboardService.getLoanTransactionActivities(startDate, endDate);

        return ApiResponse.<Map<String, Object>>builder()
                .message("Loan transaction activities retrieved successfully")
                .result(response)
                .build();
    }
}