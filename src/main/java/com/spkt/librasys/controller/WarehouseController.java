package com.spkt.librasys.controller;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.request.warehouse.WarehouseRequest;
import com.spkt.librasys.dto.response.ApiResponse;
import com.spkt.librasys.dto.response.warehouse.WarehouseResponse;
import com.spkt.librasys.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    public ApiResponse<WarehouseResponse> createWarehouse(@Valid @RequestBody WarehouseRequest request) {
        WarehouseResponse response = warehouseService.createWarehouse(request);
        return ApiResponse.<WarehouseResponse>builder()
                .message("Warehouse created successfully")
                .result(response)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<WarehouseResponse> updateWarehouse(
            @PathVariable Long id,
            @Valid @RequestBody WarehouseRequest request) {
        WarehouseResponse response = warehouseService.updateWarehouse(id, request);
        return ApiResponse.<WarehouseResponse>builder()
                .message("Warehouse updated successfully")
                .result(response)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<WarehouseResponse> getWarehouseById(@PathVariable Long id) {
        WarehouseResponse response = warehouseService.getWarehouseById(id);
        return ApiResponse.<WarehouseResponse>builder()
                .message("Warehouse retrieved successfully")
                .result(response)
                .build();
    }

    @GetMapping
    public ApiResponse<PageDTO<WarehouseResponse>> getAllWarehouses(Pageable pageable) {
        PageDTO<WarehouseResponse> response = warehouseService.getAllWarehouses(pageable);
        return ApiResponse.<PageDTO<WarehouseResponse>>builder()
                .message("Warehouses retrieved successfully")
                .result(response)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return ApiResponse.<Void>builder()
                .message("Warehouse deleted successfully")
                .build();
    }
}
