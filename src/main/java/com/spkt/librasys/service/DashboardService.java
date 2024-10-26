package com.spkt.librasys.service;

import com.spkt.librasys.dto.response.dashboard.DashboardDocumentCountResponse;
import com.spkt.librasys.dto.response.dashboard.DashboardLoanTransactionCountResponse;
import com.spkt.librasys.dto.response.dashboard.DashboardTopBorrowedDocumentsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface DashboardService {
    DashboardDocumentCountResponse getDocumentCount();
    DashboardLoanTransactionCountResponse getLoanTransactionsCount(int month, int year);
    Page<DashboardTopBorrowedDocumentsResponse> getTopBorrowedDocuments(PageRequest pageRequest);
    Long getNewUsersCount(int month, int year);
    Long getUnpaidFinesCount();
    Long getUnreturnedDocumentsCount();
    Long getMonthlyActiveUsersCount(int month, int year);
}