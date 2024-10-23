package com.spkt.librasys.dto.request.loanTransaction;

import com.spkt.librasys.entity.LoanTransaction;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoanTransactionSearchRequest {

    LoanTransaction.Status status;  // Trạng thái của giao dịch (APPROVED, PENDING, REJECTED, etc.)

    String username;  // Tên người dùng

    String documentName;  // Tên tài liệu
}
