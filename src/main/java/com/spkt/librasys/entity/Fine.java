package com.spkt.librasys.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "fines_001")
public class Fine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fine_id")
    long fineId;

    @Column(name = "amount", nullable = false)
    Double amount;  // Số tiền phạt

    @Column(name = "status", nullable = false)
    String status;  // Trạng thái của khoản phạt (VD: Paid, Unpaid)

    @Column(name = "issued_date", nullable = false)
    LocalDateTime issuedDate;  // Ngày phát sinh phạt

    @Column(name = "reason")
    String reason;  // Lý do bị phạt

    // Mối quan hệ một-một với LoanTransaction
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_transaction_id", nullable = false)
    LoanTransaction transactionLoan;

    // Mối quan hệ nhiều-một với User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    User user;
}
