package com.spkt.librasys.repository;

import com.spkt.librasys.entity.Document;
import com.spkt.librasys.entity.LoanTransaction;
import com.spkt.librasys.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
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

    boolean existsByUserAndDocumentAndStatusIn(User user, Document document, Collection<LoanTransaction.Status> statuses);

    boolean existsByUserAndDocumentAndStatusNotIn(User user, Document document, Collection<LoanTransaction.Status> excludedStatuses);

    Page<LoanTransaction> findByUser(User user, Pageable pageable);

    // Tổng số lượt mượn và trả sách trong từng tháng
    @Query("SELECT FUNCTION('MONTH', lt.loanDate) AS month, COUNT(lt) AS loanCount " +
            "FROM loan_transaction_001 lt " +
            "WHERE FUNCTION('YEAR', lt.loanDate) = :year " +
            "GROUP BY FUNCTION('MONTH', lt.loanDate) " +
            "ORDER BY FUNCTION('MONTH', lt.loanDate)")
    List<Object[]> countLoansByMonth(@Param("year") int year);

    // Thống kê số lượng người vi phạm (phải đóng phạt)
    @Query("SELECT COUNT(DISTINCT f.user.userId) " +
            "FROM fines_001 f " +
            "JOIN f.transactionLoan l " +
            "WHERE f.status = com.spkt.librasys.entity.Fine.Status.UNPAID")
    long countViolators();

    // Số lượng sách trả quá hạn trong từng tháng của năm
    @Query("SELECT FUNCTION('MONTH', lt.returnDate) AS month, COUNT(lt) AS overdueCount " +
            "FROM loan_transaction_001 lt " +
            "WHERE lt.returnDate > lt.dueDate " +
            "AND FUNCTION('YEAR', lt.returnDate) = :year " +
            "GROUP BY FUNCTION('MONTH', lt.returnDate) " +
            "ORDER BY FUNCTION('MONTH', lt.returnDate)")
    List<Object[]> countOverdueBooksByYear(@Param("year") int year);

    // Số lượng sách bị hư hỏng trong từng tháng của năm
    @Query("SELECT FUNCTION('MONTH', lt.returnDate) AS month, COUNT(lt) AS damagedCount " +
            "FROM loan_transaction_001 lt " +
            "WHERE lt.returnCondition = com.spkt.librasys.entity.LoanTransaction.Condition.DAMAGED " +
            "AND FUNCTION('YEAR', lt.returnDate) = :year " +
            "GROUP BY FUNCTION('MONTH', lt.returnDate) " +
            "ORDER BY FUNCTION('MONTH', lt.returnDate)")
    List<Object[]> countDamagedBooksByYear(@Param("year") int year);

    // Bảng thống kê hoạt động mượn trả
    @Query("SELECT lt.user.userId, lt.user.firstName, lt.user.lastName, lt.document.documentName, " +
            "lt.loanDate, lt.returnDate, lt.status " +
            "FROM loan_transaction_001 lt " +
            "WHERE lt.loanDate BETWEEN :startDate AND :endDate")
    List<Object[]> getLoanTransactionActivities(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


}
