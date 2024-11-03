package com.spkt.librasys.service.impl;

import com.spkt.librasys.dto.response.dashboard.DashboardDocumentCountResponse;
import com.spkt.librasys.dto.response.dashboard.DashboardLoanTransactionCountResponse;
import com.spkt.librasys.dto.response.dashboard.DashboardTopBorrowedDocumentsResponse;
import com.spkt.librasys.entity.Fine;
import com.spkt.librasys.repository.FineRepository;
import com.spkt.librasys.repository.LoanTransactionRepository;
import com.spkt.librasys.repository.access.UserRepository;
import com.spkt.librasys.repository.DocumentRepository;
import com.spkt.librasys.service.DashboardService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DashboardServiceImpl implements DashboardService {

    DocumentRepository documentRepository;
    LoanTransactionRepository loanTransactionRepository;
    UserRepository userRepository;
    FineRepository fineRepository;

    @Override
    public DashboardDocumentCountResponse getDocumentCount() {
        long count = documentRepository.count();
        return new DashboardDocumentCountResponse(count);
    }

    @Override
    public DashboardLoanTransactionCountResponse getLoanTransactionsCount(int month, int year) {
        long count = loanTransactionRepository.countByMonthAndYear(month, year);
        return new DashboardLoanTransactionCountResponse(count);
    }

    @Override
    public Page<DashboardTopBorrowedDocumentsResponse> getTopBorrowedDocuments(PageRequest pageRequest) {
        return documentRepository.findTopBorrowedDocuments(pageRequest)
                .map(doc -> new DashboardTopBorrowedDocumentsResponse((String) doc[0], (Long) doc[1]));
    }

    @Override
    public Long getNewUsersCount(int month, int year) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return userRepository.countNewUsersInCurrentMonth(start, end);
    }

    @Override
    public Long getUnpaidFinesCount() {
        return fineRepository.countByStatus(Fine.Status.UNPAID);
    }

    @Override
    public Long getUnreturnedDocumentsCount() {
        return loanTransactionRepository.countByReturnDateIsNull();
    }

    @Override
    public Long getMonthlyActiveUsersCount(int month, int year) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return userRepository.countActiveUsersInCurrentMonth(start, end);
    }
}