package com.spkt.librasys.repository;

import com.spkt.librasys.entity.Document;
import com.spkt.librasys.entity.LoanTransaction;
import com.spkt.librasys.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LoanTransactionRepository extends JpaRepository<LoanTransaction, Long>, JpaSpecificationExecutor<LoanTransaction> {

    List<LoanTransaction> findAllByStatusAndCreatedAtBefore(LoanTransaction.Status status, LocalDateTime dateTime);

    // Dashboard
    @Query("SELECT COUNT(lt) FROM loan_transaction_001 lt WHERE MONTH(lt.loanDate) = :month AND YEAR(lt.loanDate) = :year")
    long countByMonthAndYear(@Param("month") int month, @Param("year") int year);


    @Query("SELECT COUNT(l) FROM loan_transaction_001 l WHERE l.returnDate IS NULL")
    Long countByReturnDateIsNull();

    Long countByUserAndStatus(User user, LoanTransaction.Status status);

    Long countByDocumentAndStatus(Document document, LoanTransaction.Status status);
}
