package com.spkt.librasys.repository;


import com.spkt.librasys.entity.LoanTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanTransactionRepository extends JpaRepository<LoanTransaction, String> {
    List<LoanTransaction> findByUserUserId(String userId);
}
