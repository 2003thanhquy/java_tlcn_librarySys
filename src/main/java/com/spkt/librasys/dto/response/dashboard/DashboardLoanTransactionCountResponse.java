package com.spkt.librasys.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DashboardLoanTransactionCountResponse {
    private long loanTransactionCount;
}