package com.spkt.librasys.mapper;

import com.spkt.librasys.dto.response.document.DocumentTypeResponse;
import com.spkt.librasys.dto.response.fine.FineResponse;
import com.spkt.librasys.entity.DocumentType;
import com.spkt.librasys.entity.Fine;
import com.spkt.librasys.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = LoanTransactionMapper.class)
public interface FineMapper {
    @Mapping(target = "loanTransactionResponse", source = "transactionLoan")
    FineResponse toFineResponse(Fine fine);
}
