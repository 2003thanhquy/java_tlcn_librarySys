package com.spkt.librasys.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "loan_transaction_001")
public class LoanTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    long transactionId;

    @Column(name = "loan_date", nullable = false)
    LocalDate loanDate;  // Ngày mượn sách

    @Column(name = "due_date", nullable = false)
    LocalDate dueDate;  // Ngày dự kiến trả

    @Column(name = "return_date")
    LocalDate returnDate;  // Ngày trả sách thực tế (có thể null nếu chưa trả)

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
}
