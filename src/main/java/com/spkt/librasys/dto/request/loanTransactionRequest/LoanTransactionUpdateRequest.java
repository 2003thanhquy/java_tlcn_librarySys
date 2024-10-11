package com.spkt.librasys.dto.request.loanTransactionRequest;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoanTransactionUpdateRequest {
    LocalDate returnDate;
    String status; // PENDING, APPROVED, RETURNED
}