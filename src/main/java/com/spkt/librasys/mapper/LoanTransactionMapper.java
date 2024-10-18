package com.spkt.librasys.mapper;

import com.spkt.librasys.dto.request.loanTransaction.LoanTransactionRequest;
import com.spkt.librasys.dto.response.LoanTransactionResponse;
import com.spkt.librasys.entity.LoanTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanTransactionMapper {

    @Mapping(target = "documentName", source = "document.documentName")
    @Mapping(target = "username", source = "user.username")
    LoanTransactionResponse toLoanTransactionResponse(LoanTransaction loanTransaction);

    LoanTransaction toLoanTransaction(LoanTransactionRequest loanTransactionRequest);
}
