package com.spkt.librasys.dto.response;

import com.spkt.librasys.entity.LoanTransaction;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
    LoanTransaction.Status status;  // Trạng thái của giao dịch
}
