package com.spkt.librasys.service;

import com.spkt.librasys.dto.response.dashboard.DashboardDocumentCountResponse;
import com.spkt.librasys.dto.response.dashboard.DashboardLoanTransactionCountResponse;
import com.spkt.librasys.dto.response.dashboard.DashboardTopBorrowedDocumentsResponse;

public interface DashboardService {
    DashboardDocumentCountResponse getDocumentCount();
    DashboardLoanTransactionCountResponse getLoanTransactionsCount();
    DashboardTopBorrowedDocumentsResponse getTopBorrowedDocuments();
    Long getNewUsersCount();
    Long getUnpaidFinesCount();
    Long getUnreturnedDocumentsCount();
    Long getMonthlyActiveUsersCount();
}
