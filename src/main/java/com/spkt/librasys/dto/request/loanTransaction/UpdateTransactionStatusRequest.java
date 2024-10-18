package com.spkt.librasys.dto.request.loanTransaction;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTransactionStatusRequest {
//    private LoanTransactionStatus status;
    private Boolean isApproved;
}
