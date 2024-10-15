package com.spkt.librasys.repository;

import com.spkt.librasys.entity.LoanTransaction;
import com.spkt.librasys.entity.enums.LoanTransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface LoanTransactionRepository extends JpaRepository<LoanTransaction, Long>, JpaSpecificationExecutor<LoanTransaction> {
    List<LoanTransaction> findAllByStatusAndCreatedAtBefore(LoanTransactionStatus status, LocalDateTime dateTime);
}
