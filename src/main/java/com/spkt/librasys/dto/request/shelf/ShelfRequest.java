package com.spkt.librasys.dto.request.shelf;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ShelfRequest {

    @NotBlank(message = "Shelf number is required")
    private String shelfNumber;

    @NotNull(message = "Zone ID is required")
    private Long zoneId; // ID của DisplayZone mà Shelf thuộc về
}
