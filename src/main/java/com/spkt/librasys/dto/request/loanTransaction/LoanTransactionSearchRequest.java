package com.spkt.librasys.dto.request.loanTransaction;

import com.spkt.librasys.entity.enums.LoanTransactionStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoanTransactionSearchRequest {

    LoanTransactionStatus status;  // Trạng thái của giao dịch (APPROVED, PENDING, REJECTED, etc.)

    String username;  // Tên người dùng

    String documentName;  // Tên tài liệu
}
