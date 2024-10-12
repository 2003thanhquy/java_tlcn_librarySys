package com.spkt.librasys.mapper;

import com.spkt.librasys.dto.response.accessHistoryResponse.AccessHistoryResponse;
import com.spkt.librasys.entity.AccessHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccessHistoryMapper {
    @Mapping(source = "document.documentId", target = "documentId", resultType = Long.class)
    @Mapping(source = "user.userId", target = "userId", resultType = String.class)
    AccessHistoryResponse toAccessHistoryResponse(AccessHistory accessHistory);
}
