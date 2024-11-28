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

/**
 * Controller quản lý các yêu cầu liên quan đến kho trong hệ thống.
 * Các API này cho phép tạo mới, cập nhật, xóa và lấy thông tin kho.
 */
@RestController
@RequestMapping("/api/v1/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    /**
     * Tạo mới một kho trong hệ thống.
     *
     * @param request yêu cầu tạo kho mới
     * @return ApiResponse chứa thông tin kho vừa tạo
     */
    @PostMapping
    public ApiResponse<WarehouseResponse> createWarehouse(@Valid @RequestBody WarehouseRequest request) {
        WarehouseResponse response = warehouseService.createWarehouse(request);
        return ApiResponse.<WarehouseResponse>builder()
                .message("Tạo kho thành công")
                .result(response)
                .build();
    }

    /**
     * Cập nhật thông tin của kho theo ID.
     *
     * @param id ID của kho cần cập nhật
     * @param request yêu cầu cập nhật thông tin kho
     * @return ApiResponse chứa thông tin kho sau khi cập nhật
     */
    @PutMapping("/{id}")
    public ApiResponse<WarehouseResponse> updateWarehouse(
            @PathVariable Long id,
            @Valid @RequestBody WarehouseRequest request) {
        WarehouseResponse response = warehouseService.updateWarehouse(id, request);
        return ApiResponse.<WarehouseResponse>builder()
                .message("Cập nhật kho thành công")
                .result(response)
                .build();
    }

    /**
     * Lấy thông tin kho theo ID.
     *
     * @param id ID của kho cần lấy thông tin
     * @return ApiResponse chứa thông tin kho
     */
    @GetMapping("/{id}")
    public ApiResponse<WarehouseResponse> getWarehouseById(@PathVariable Long id) {
        WarehouseResponse response = warehouseService.getWarehouseById(id);
        return ApiResponse.<WarehouseResponse>builder()
                .message("Lấy thông tin kho thành công")
                .result(response)
                .build();
    }

    /**
     * Lấy danh sách tất cả các kho trong hệ thống với phân trang.
     *
     * @param pageable thông tin phân trang
     * @return ApiResponse chứa danh sách kho với phân trang
     */
    @GetMapping
    public ApiResponse<PageDTO<WarehouseResponse>> getAllWarehouses(Pageable pageable) {
        PageDTO<WarehouseResponse> response = warehouseService.getAllWarehouses(pageable);
        return ApiResponse.<PageDTO<WarehouseResponse>>builder()
                .message("Lấy danh sách kho thành công")
                .result(response)
                .build();
    }

    /**
     * Xóa kho theo ID.
     *
     * @param id ID của kho cần xóa
     * @return ApiResponse thông báo xóa kho thành công
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return ApiResponse.<Void>builder()
                .message("Xóa kho thành công")
                .build();
    }
}
