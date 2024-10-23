package com.spkt.librasys.repository;

import com.spkt.librasys.entity.LoanTransaction;
import com.spkt.librasys.entity.RenewalHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RenewalHistoryRepository extends JpaRepository<RenewalHistory, Long> {
    List<RenewalHistory> findByLoanTransaction(LoanTransaction loanTransaction);
}
