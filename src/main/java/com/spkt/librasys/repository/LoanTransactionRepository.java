package com.spkt.librasys.repository;

import com.spkt.librasys.entity.Document;
import com.spkt.librasys.entity.LoanTransaction;
import com.spkt.librasys.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface LoanTransactionRepository extends JpaRepository<LoanTransaction, Long>, JpaSpecificationExecutor<LoanTransaction> {
    List<LoanTransaction> findAllByStatusAndCreatedAtBefore(LoanTransaction.Status status, LocalDateTime dateTime);

    //dashboard
    //@Query("SELECT COUNT(l) FROM LoanTransaction l WHERE l.returnDate IS NULL")
    Long countByReturnDateIsNull();
    Long countByUserAndStatus(User user, LoanTransaction.Status status);
    Long countByDocumentAndStatus(Document document, LoanTransaction.Status status);
}
