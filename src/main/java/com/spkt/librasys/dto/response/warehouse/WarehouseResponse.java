package com.spkt.librasys.dto.response.warehouse;

import lombok.Data;

@Data
public class WarehouseResponse {
    private Long warehouseId;
    private String warehouseName;
    private String location;
}
