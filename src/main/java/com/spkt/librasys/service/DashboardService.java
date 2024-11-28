package com.spkt.librasys.service;

import com.spkt.librasys.dto.response.dashboard.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Interface cung cấp các phương thức xử lý và thu thập dữ liệu thống kê cho Dashboard.
 */
public interface DashboardService {

    /**
     * Lấy thông tin số lượng tài liệu trong hệ thống.
     *
     * @return Thông tin về số lượng tài liệu trong hệ thống.
     */
    DashboardDocumentCountResponse getDocumentCount();

    /**
     * Lấy thông tin số lượng giao dịch mượn trả trong tháng và năm cụ thể.
     *
     * @param month Tháng cần lấy dữ liệu.
     * @param year Năm cần lấy dữ liệu.
     * @return Thông tin về số lượng giao dịch mượn trả trong tháng và năm.
     */
    DashboardLoanTransactionCountResponse getLoanTransactionsCount(int month, int year);

    /**
     * Lấy danh sách các tài liệu được mượn nhiều nhất, hỗ trợ phân trang.
     *
     * @param pageRequest Thông tin phân trang.
     * @return Danh sách các tài liệu được mượn nhiều nhất.
     */
    Page<DashboardTopBorrowedDocumentsResponse> getTopBorrowedDocuments(PageRequest pageRequest);

    /**
     * Lấy số lượng người dùng mới trong tháng và năm cụ thể.
     *
     * @param month Tháng cần lấy dữ liệu.
     * @param year Năm cần lấy dữ liệu.
     * @return Số lượng người dùng mới trong tháng và năm.
     */
    Long getNewUsersCount(int month, int year);

    /**
     * Lấy số lượng các khoản phạt chưa được thanh toán.
     *
     * @return Số lượng các khoản phạt chưa được thanh toán.
     */
    Long getUnpaidFinesCount();

    /**
     * Lấy số lượng tài liệu chưa được trả lại.
     *
     * @return Số lượng tài liệu chưa được trả lại.
     */
    Long getUnreturnedDocumentsCount();

    /**
     * Lấy số lượng người dùng hoạt động trong tháng cụ thể.
     *
     * @param month Tháng cần lấy dữ liệu.
     * @param year Năm cần lấy dữ liệu.
     * @return Số lượng người dùng hoạt động trong tháng.
     */
    Long getMonthlyActiveUsersCount(int month, int year);

    /**
     * Lấy số lượng người dùng hoạt động trong năm cụ thể.
     *
     * @param year Năm cần lấy dữ liệu.
     * @return Số lượng người dùng hoạt động trong năm.
     */
    Long getYearlyActiveUsersCount(int year);

    /**
     * Lấy thống kê về người dùng theo tháng và năm cụ thể.
     *
     * @param month Tháng cần lấy thống kê.
     * @param year Năm cần lấy thống kê.
     * @return Thông tin thống kê về người dùng.
     */
    UserStatisticsResponse getUserStatistics(Integer month, Integer year);

    /**
     * Lấy thống kê về tài liệu trong năm cụ thể.
     *
     * @param year Năm cần lấy thống kê.
     * @return Thông tin thống kê về tài liệu.
     */
    DocumentStatisticsResponse getDocumentStatistics(String year);

    /**
     * Lấy thống kê về các giao dịch mượn trả trong năm cụ thể.
     *
     * @param year Năm cần lấy thống kê.
     * @return Thông tin thống kê về các giao dịch mượn trả.
     */
    LoanTransactionStatisticsResponse getLoanTransactionStatistics(int year);

    /**
     * Lấy các hoạt động giao dịch mượn trả trong khoảng thời gian cụ thể.
     *
     * @param startDate Ngày bắt đầu.
     * @param endDate Ngày kết thúc.
     * @return Một bản đồ chứa các hoạt động giao dịch mượn trả.
     */
    Map<String, Object> getLoanTransactionActivities(LocalDate startDate, LocalDate endDate);
}
