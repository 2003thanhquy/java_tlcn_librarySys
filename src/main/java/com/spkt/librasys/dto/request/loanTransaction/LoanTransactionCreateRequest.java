package com.spkt.librasys.dto.request.loanTransaction;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoanTransactionCreateRequest {
    LocalDate loanDate;
    LocalDate dueDate;
    String documentId;
    String userId;
}