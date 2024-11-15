package com.spkt.librasys.dto.response.displayzone;

import lombok.Data;

import java.util.List;

@Data
public class DisplayZoneResponse {
    private Long zoneId;
    private String zoneName;
    private List<String> shelves; // Danh sách tên của các Shelf
}
