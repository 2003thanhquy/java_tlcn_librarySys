package com.spkt.librasys.mapper;

import com.spkt.librasys.dto.request.rack.RackRequest;
import com.spkt.librasys.dto.response.rack.RackResponse;
import com.spkt.librasys.entity.Rack;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RackMapper {

    @Mapping(target = "shelf.shelfId", source = "shelfId") // Map shelfId từ RackRequest tới Shelf trong Rack
    Rack toEntity(RackRequest request);

    @Mapping(target = "shelfName", source = "shelf.shelfNumber") // Map tên Shelf từ Shelf trong RackResponse
    RackResponse toResponse(Rack rack);
}
