package com.spkt.librasys.mapper;

import com.spkt.librasys.dto.request.displayzone.DisplayZoneRequest;
import com.spkt.librasys.dto.response.displayzone.DisplayZoneResponse;
import com.spkt.librasys.entity.DisplayZone;
import com.spkt.librasys.entity.Shelf;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DisplayZoneMapper {

    DisplayZone toEntity(DisplayZoneRequest request);

    @Mapping(target = "shelves", source = "shelves", qualifiedByName = "mapShelfNames")
    DisplayZoneResponse toResponse(DisplayZone displayZone);

    // Phương thức ánh xạ tùy chỉnh
    @Named("mapShelfNames")
    default List<String> mapShelfNames(List<Shelf> shelves) {
        return shelves.stream()
                .map(Shelf::getShelfNumber) // Lấy `shelfNumber` từ đối tượng Shelf
                .collect(Collectors.toList());
    }
}
