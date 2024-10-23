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
@Entity(name = "renewal_history_001")
public class RenewalHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_transaction_id", nullable = false)
    LoanTransaction loanTransaction;

    @Column(name = "renewal_date", nullable = false)
    LocalDateTime renewalDate; // Ngày gia hạn

    @Column(name = "extended_due_date", nullable = false)
    LocalDate extendedDueDate; // Ngày trả mới sau gia hạn

    @Column(name = "renewed_by", nullable = false)
    String renewedBy; // Người thực hiện gia hạn (admin, user...)
} 