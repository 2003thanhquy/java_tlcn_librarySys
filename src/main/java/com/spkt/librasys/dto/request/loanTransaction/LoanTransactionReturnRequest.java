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

//    @NotNull(message = "Return date cannot be null")
//    LocalDateTime returnDate;  // Ngày trả sách thực tế

    @NotNull(message = "Book condition must be specified")
    Boolean isBookDamaged;  // Trạng thái sách có bị hư hỏng không (true: hư hỏng, false: bình thường)

    @PositiveOrZero(message = "Fine amount must be zero or positive")
    Double fineAmount;  // Số tiền phạt (nếu có)

    String fineReason;  // Lý do bị phạt (nếu có phạt)

    @NotBlank(message = "Fine status must be provided if there is a fine")
    Fine.Status status;  // Trạng thái của khoản phạt (Paid, Unpaid, etc.)
}
