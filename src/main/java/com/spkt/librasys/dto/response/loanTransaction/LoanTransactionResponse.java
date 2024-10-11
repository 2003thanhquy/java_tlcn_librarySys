package com.spkt.librasys.dto.response.loanTransaction;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoanTransactionResponse {
    String transactionId;
    LocalDate loanDate;
    LocalDate dueDate;
    LocalDate returnDate;
    String status;
    String documentId;
    String userId;
}