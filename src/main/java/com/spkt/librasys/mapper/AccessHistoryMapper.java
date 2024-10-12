package com.spkt.librasys.mapper;

import com.spkt.librasys.dto.response.accessHistoryResponse.AccessHistoryResponse;
import com.spkt.librasys.entity.AccessHistory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccessHistoryMapper {
    AccessHistoryResponse toAccessHistoryResponse(AccessHistory accessHistory);
}
