package com.spkt.librasys.dto.request.loanTransaction;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoanTransactionRequest {

    @NotNull(message = "Document ID cannot be null")
    Long documentId;  // Tài liệu cần mượn

//    @NotNull(message = "Loan date cannot be null")
//    LocalDateTime loanDate;  // Ngày mượn sách

}
