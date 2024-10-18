package com.spkt.librasys.dto.response.fine;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FineResponse {
    String fineId;
    double amount;
    String status;  // UNPAID, PAID
    String loanTransactionId;
}