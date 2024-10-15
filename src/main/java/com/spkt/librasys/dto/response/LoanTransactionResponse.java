package com.spkt.librasys.dto.response;

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
public class LoanTransactionResponse {
    Long transactionId;
    String documentName;
    String username;  
    LocalDate loanDate;  
    LocalDate dueDate;  
    LocalDate returnDate;  
    LoanTransactionStatus status;  // Trạng thái của giao dịch
}
