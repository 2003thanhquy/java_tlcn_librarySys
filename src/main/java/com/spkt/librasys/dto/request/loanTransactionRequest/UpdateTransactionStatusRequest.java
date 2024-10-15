package com.spkt.librasys.dto.request.loanTransactionRequest;

import com.spkt.librasys.entity.enums.LoanTransactionStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTransactionStatusRequest {
//    private LoanTransactionStatus status;
    private Boolean isApproved;
}
