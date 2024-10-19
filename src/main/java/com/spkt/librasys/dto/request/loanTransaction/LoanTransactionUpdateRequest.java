package com.spkt.librasys.dto.request.loanTransaction;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoanTransactionUpdateRequest {
    @NotNull(message = "Transaction ID cannot be null")
    Long transactionId;

    @NotNull(message = "Action type is required")
    ActionType action;

    public enum ActionType {
        RECEIVE, RETURN_REQUEST, RETURN, CANCEL, APPROVE, REJECTED;
    }
}