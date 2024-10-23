package com.spkt.librasys.service.impl;

import com.spkt.librasys.dto.response.dashboard.DashboardDocumentCountResponse;
import com.spkt.librasys.dto.response.dashboard.DashboardLoanTransactionCountResponse;
import com.spkt.librasys.dto.response.dashboard.DashboardTopBorrowedDocumentsResponse;
import com.spkt.librasys.entity.Fine;
import com.spkt.librasys.repository.FineRepository;
import com.spkt.librasys.repository.access.UserRepository;
import com.spkt.librasys.repository.document.DocumentRepository;
import com.spkt.librasys.repository.LoanTransactionRepository;
import com.spkt.librasys.service.DashboardService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DashboardServiceImpl implements DashboardService {

    DocumentRepository documentRepository;
    LoanTransactionRepository loanTransactionRepository;
    FineRepository fineRepository;
    UserRepository userRepository;

    @Override
    public DashboardDocumentCountResponse getDocumentCount() {
        long count = documentRepository.count();
        return new DashboardDocumentCountResponse(count);
    }

    @Override
    public DashboardLoanTransactionCountResponse getLoanTransactionsCount() {
        long count = loanTransactionRepository.count(); // Có thể thêm điều kiện theo tháng
        return new DashboardLoanTransactionCountResponse(count);
    }

    @Override
    public DashboardTopBorrowedDocumentsResponse getTopBorrowedDocuments() {
        // Lấy ra danh sách top tài liệu được mượn nhiều nhất
        List<String> topBorrowedDocuments = documentRepository.findTopBorrowedDocuments();
        return new DashboardTopBorrowedDocumentsResponse(topBorrowedDocuments);
    }
    @Override
    public Long getUnpaidFinesCount() {
        // Đếm số lượng khoản phạt chưa thanh toán
        return fineRepository.countByStatus(Fine.Status.UNPAID);
    }

    @Override
    public Long getUnreturnedDocumentsCount() {
        // Đếm số lượng tài liệu chưa được trả
        return loanTransactionRepository.countByReturnDateIsNull();
    }

    @Override
    public Long getNewUsersCount() {
        LocalDate start = LocalDate.now().withDayOfMonth(1); // Đầu tháng hiện tại
        LocalDate end = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()); // Cuối tháng hiện tại
        Long count = userRepository.countNewUsersInCurrentMonth(start, end);
        return count != null ? count : 0L;
    }

    @Override
    public Long getMonthlyActiveUsersCount() {
        LocalDate start = LocalDate.now().withDayOfMonth(1); // Đầu tháng hiện tại
        LocalDate end = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()); // Cuối tháng hiện tại
        Long count = userRepository.countActiveUsersInCurrentMonth(start, end);
        return count != null ? count : 0L;
    }

}

