package com.spkt.librasys.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "loan_transaction_001")
@Table(name = "loan_transaction_001", indexes = {
        @Index(name = "idx_loan_date", columnList = "loan_date"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_user_id", columnList = "user_id")
})
public class LoanTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    long transactionId;

    @Column(name = "loan_date")
    LocalDateTime loanDate;  // Ngày mượn sách

    @Column(name = "due_date")
    LocalDate dueDate;  // Ngày dự kiến trả

    @Column(name = "return_date")
    LocalDateTime returnDate;  // Ngày trả sách thực tế (có thể null nếu chưa trả)

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    Status status;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @Column(name = "vnp_txn_ref") // Trường để lưu vnp_TxnRef
    String vnpTxnRef;

    @Column(name = "return_condition", length = 20)
    @Enumerated(EnumType.STRING)
    Condition returnCondition; // Trạng thái của sách khi trả lại


//    @Column(name = "max_renewals", nullable = false)
//    int maxRenewals = 2; // Số lần gia hạn tối đa cho người dùng

    // Mối quan hệ ngược lại với Fine (một LoanTransaction có một Fine)
    @OneToOne(mappedBy = "transactionLoan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Fine fine;

    // Mối quan hệ nhiều-một với Document (tài liệu mượn)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    Document document;

    // Mối quan hệ nhiều-một với User (người mượn)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    // Thêm các trường mới để lưu trữ vị trí gốc
    @Column(name = "original_rack_id")
    Long originalRackId;

    @Column(name = "original_warehouse_id")
    Long originalWarehouseId;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    public boolean isOverdue() {
        return returnDate == null && dueDate.isBefore(LocalDate.now());
    }
    public enum Status {
        PENDING,RECEIVED,RETURN_REQUESTED, RETURNED, CANCELLED_BY_USER,CANCELLED_AUTO, APPROVED,REJECTED
    }
    public enum Condition {
        NORMAL, // Sách trong tình trạng bình thường
        DAMAGED // Sách bị hư hỏng
    }
}
