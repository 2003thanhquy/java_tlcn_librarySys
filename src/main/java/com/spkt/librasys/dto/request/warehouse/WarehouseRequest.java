package com.spkt.librasys.dto.request.warehouse;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WarehouseRequest {

    @NotBlank(message = "Warehouse name is required")
    private String warehouseName;

    @NotBlank(message = "Location is required")
    private String location;
}
