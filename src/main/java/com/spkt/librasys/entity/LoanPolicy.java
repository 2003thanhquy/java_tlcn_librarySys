package com.spkt.librasys.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "loan_policy")
public class LoanPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long policyId;

//    @Column(name = "user_role", nullable = false)
//    String userRole;  // Ví dụ: Sinh viên, Giảng viên

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_type_id", nullable = false)
    DocumentType documentType;  // Quan hệ ManyToOne với DocumentType

    @Column(name = "max_loan_days", nullable = false)
    int maxLoanDays;  // Số ngày tối đa có thể mượn

    @Column(name = "max_renewals", nullable = false)
    int maxRenewals; // Số lần tối đa có thể gia hạn

    // Các trường và phương thức khác...
}
