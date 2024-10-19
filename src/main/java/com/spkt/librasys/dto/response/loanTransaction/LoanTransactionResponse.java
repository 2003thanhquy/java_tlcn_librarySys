package com.spkt.librasys.dto.response.loanTransaction;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoanTransactionResponse {

    long transactionId;
    LocalDateTime loanDate;  // Ngày mượn sách
    LocalDate returnDate;  // Ngày trả sách (nếu có)
    String status;  // Trạng thái của giao dịch
    LocalDateTime createdAt;  // Ngày tạo
    LocalDateTime updatedAt;  // Ngày cập nhật

    String documentName;  // Tên tài liệu
    String username;  // Tên người mượn
}
