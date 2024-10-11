package com.spkt.librasys.dto.request.fineRequest;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FineCreateRequest {
    double amount;
    String status;  // UNPAID, PAID
    String loanTransactionId;
}