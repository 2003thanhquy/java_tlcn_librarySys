package com.spkt.librasys.dto.request.loanTransactionRequest;

import lombok.*;
import lombok.experimental.FieldDefaults;
import com.spkt.librasys.entity.enums.LoanTransactionStatus;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoanTransactionRequest {
    Long documentId;
    LocalDate dueDate;
    LoanTransactionStatus status = LoanTransactionStatus.PENDING; // Trạng thái mặc định là PENDING
}
