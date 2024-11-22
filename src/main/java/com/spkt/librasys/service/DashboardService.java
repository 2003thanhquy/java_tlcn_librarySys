package com.spkt.librasys.service;

import com.spkt.librasys.dto.response.dashboard.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface DashboardService {
    DashboardDocumentCountResponse getDocumentCount();
    DashboardLoanTransactionCountResponse getLoanTransactionsCount(int month, int year);
    Page<DashboardTopBorrowedDocumentsResponse> getTopBorrowedDocuments(PageRequest pageRequest);
    Long getNewUsersCount(int month, int year);
    Long getUnpaidFinesCount();
    Long getUnreturnedDocumentsCount();
    Long getMonthlyActiveUsersCount(int month, int year);
    UserStatisticsResponse getUserStatistics(Integer month, Integer year);

    DocumentStatisticsResponse getDocumentStatistics(String year);

    LoanTransactionStatisticsResponse getLoanTransactionStatistics(int year);
    Map<String, Object> getLoanTransactionActivities(LocalDate startDate, LocalDate endDate);

}

