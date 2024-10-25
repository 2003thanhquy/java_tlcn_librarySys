package com.spkt.librasys.dto.request.loanTransaction;

import com.spkt.librasys.entity.Fine;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoanTransactionReturnRequest {

    @NotNull(message = "Transaction ID cannot be null")
    Long transactionId;  // ID của giao dịch mượn sách

    @NotNull(message = "Book condition must be specified")
    Boolean isBookDamaged;  // Trạng thái sách có bị hư hỏng không (true: hư hỏng, false: bình thường)

    @NotNull(message = "Fine amount cannot be null")
    @PositiveOrZero(message = "Fine amount must be zero or positive")
    Double fineAmount;  // Số tiền phạt (nếu có)

    @NotNull(message = "Fine reason cannot be null")
    @NotBlank(message = "Fine reason must be provided if there is a fine")
    String fineReason;  // Lý do bị phạt (nếu có phạt)

    @NotNull(message = "Fine status must be provided if there is a fine")
    Fine.Status status;  // Trạng thái của khoản phạt (PAID,UNPAID, etc.)
}
