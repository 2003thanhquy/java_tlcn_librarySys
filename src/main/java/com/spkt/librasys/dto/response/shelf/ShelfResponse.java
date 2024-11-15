package com.spkt.librasys.dto.response.shelf;

import lombok.Data;

@Data
public class ShelfResponse {

    private Long shelfId;
    private String shelfNumber;
    private String zoneName; // Tên của DisplayZone chứa Shelf
}
