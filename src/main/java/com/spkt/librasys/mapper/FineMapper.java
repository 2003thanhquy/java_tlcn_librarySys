package com.spkt.librasys.mapper;

import com.spkt.librasys.dto.response.document.DocumentTypeResponse;
import com.spkt.librasys.dto.response.fine.FineResponse;
import com.spkt.librasys.entity.DocumentType;
import com.spkt.librasys.entity.Fine;
import com.spkt.librasys.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FineMapper {
    FineResponse toFineResponse(Fine fine);
}
