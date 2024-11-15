package com.spkt.librasys.mapper;

import com.spkt.librasys.dto.request.shelf.ShelfRequest;
import com.spkt.librasys.dto.response.shelf.ShelfResponse;
import com.spkt.librasys.entity.Shelf;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShelfMapper {

    @Mapping(target = "zone.zoneId", source = "zoneId") // Map zoneId từ ShelfRequest tới DisplayZone trong Shelf
    Shelf toEntity(ShelfRequest request);

    @Mapping(target = "zoneName", source = "zone.zoneName") // Map tên zone từ DisplayZone trong ShelfResponse
    ShelfResponse toResponse(Shelf shelf);
}
