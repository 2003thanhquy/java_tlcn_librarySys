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

/**
 * Controller xử lý các yêu cầu liên quan đến thống kê trên Dashboard.
 * Các API cung cấp thông tin thống kê về tài liệu, người dùng, giao dịch mượn trả, và các dữ liệu khác.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dashboards")
@Slf4j
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Lấy tổng số tài liệu trong hệ thống.
     *
     * @return ApiResponse chứa tổng số tài liệu.
     */
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

    /**
     * Lấy số lượng giao dịch mượn sách theo tháng và năm.
     *
     * @param month Tháng cần lấy số lượng giao dịch.
     * @param year Năm cần lấy số lượng giao dịch.
     * @return ApiResponse chứa số lượng giao dịch mượn sách.
     */
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

    /**
     * Lấy danh sách tài liệu được mượn nhiều nhất, có hỗ trợ phân trang.
     *
     * @param page Số trang (default = 0).
     * @param size Số lượng tài liệu trên mỗi trang (default = 10).
     * @return ApiResponse chứa danh sách tài liệu mượn nhiều nhất.
     */
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

    /**
     * Lấy số lượng người dùng mới trong tháng và năm cụ thể.
     *
     * @param month Tháng cần lấy số lượng người dùng mới.
     * @param year Năm cần lấy số lượng người dùng mới.
     * @return ApiResponse chứa số lượng người dùng mới.
     */
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

    /**
     * Lấy số lượng khoản phạt chưa thanh toán.
     *
     * @return ApiResponse chứa số lượng khoản phạt chưa thanh toán.
     */
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

    /**
     * Lấy số lượng tài liệu chưa trả.
     *
     * @return ApiResponse chứa số lượng tài liệu chưa trả.
     */
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

    /**
     * Lấy số lượng người dùng hoạt động theo tháng hoặc năm.
     *
     * @param year Năm cần lấy số lượng người dùng hoạt động.
     * @param month Tháng cần lấy số lượng người dùng hoạt động (tùy chọn).
     * @return ApiResponse chứa số lượng người dùng hoạt động.
     */
    @GetMapping("/users/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Long> getActiveUsersCount(
            @RequestParam int year,
            @RequestParam(required = false) Integer month
    ) {
        log.info("Getting active users count for month: {} and year: {}", month, year);
        Long count;
        String message;
        if (month != null) {
            // Lấy số lượng người dùng hoạt động theo tháng
            count = dashboardService.getMonthlyActiveUsersCount(month, year);
            message = String.format("Active users count for %02d/%d retrieved successfully", month, year);
        } else {
            // Lấy số lượng người dùng hoạt động trong cả năm
            count = dashboardService.getYearlyActiveUsersCount(year);
            message = String.format("Active users count for year %d retrieved successfully", year);
        }

        return ApiResponse.<Long>builder()
                .message(message)
                .result(count)
                .build();
    }

    /**
     * API thống kê người dùng tổng hợp theo tháng và năm (tùy chọn).
     *
     * @param month Tháng cần thống kê (tùy chọn).
     * @param year Năm cần thống kê.
     * @return ApiResponse chứa thống kê người dùng.
     */
    @GetMapping("/users/statistics")
    public ApiResponse<UserStatisticsResponse> getUserStatistics(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {

        UserStatisticsResponse response = dashboardService.getUserStatistics(month, year);

        return ApiResponse.<UserStatisticsResponse>builder()
                .message("User statistics retrieved successfully")
                .result(response)
                .build();
    }

    /**
     * API thống kê tài liệu theo năm.
     *
     * @param year Năm cần thống kê (tùy chọn, mặc định là năm hiện tại).
     * @return ApiResponse chứa thống kê tài liệu.
     */
    @GetMapping("/documents/statistics")
    public ApiResponse<DocumentStatisticsResponse> getDocumentStatistics(
            @RequestParam(defaultValue = "#{T(java.time.Year).now().toString()}") String year) {

        DocumentStatisticsResponse response = dashboardService.getDocumentStatistics(year);

        return ApiResponse.<DocumentStatisticsResponse>builder()
                .message("Document statistics retrieved successfully")
                .result(response)
                .build();
    }

    /**
     * API thống kê giao dịch mượn trả theo năm.
     *
     * @param year Năm cần thống kê (tùy chọn, mặc định là năm hiện tại).
     * @return ApiResponse chứa thống kê giao dịch mượn trả.
     */
    @GetMapping("/loans/statistics")
    public ApiResponse<LoanTransactionStatisticsResponse> getLoanTransactionStatistics(
            @RequestParam(defaultValue = "#{T(java.time.Year).now().value}") int year) {

        LoanTransactionStatisticsResponse response = dashboardService.getLoanTransactionStatistics(year);

        return ApiResponse.<LoanTransactionStatisticsResponse>builder()
                .message("Loan transaction statistics retrieved successfully")
                .result(response)
                .build();
    }

    /**
     * API bảng hoạt động mượn trả trong khoảng thời gian từ startDate đến endDate.
     *
     * @param startDate Ngày bắt đầu.
     * @param endDate Ngày kết thúc.
     * @return ApiResponse chứa bảng hoạt động giao dịch mượn trả.
     */
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
