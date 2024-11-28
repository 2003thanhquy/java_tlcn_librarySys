package com.spkt.librasys.controller;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.request.rack.RackRequest;
import com.spkt.librasys.dto.response.ApiResponse;
import com.spkt.librasys.dto.response.rack.RackResponse;
import com.spkt.librasys.service.RackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/**
 * Lớp Controller xử lý các yêu cầu liên quan đến các giá đỡ (Rack).
 * Cung cấp các API để quản lý các giá đỡ trong hệ thống.
 */
@RestController
@RequestMapping("/api/v1/racks")
@RequiredArgsConstructor
public class RackController {

    private final RackService rackService;

    /**
     * API tạo mới một giá đỡ.
     * Endpoint này cho phép người dùng tạo mới một giá đỡ.
     *
     * @param request Thông tin của giá đỡ cần tạo mới.
     * @return ApiResponse chứa thông tin của giá đỡ vừa tạo.
     */
    @PostMapping
    public ApiResponse<RackResponse> createRack(@Valid @RequestBody RackRequest request) {
        RackResponse response = rackService.createRack(request);
        return ApiResponse.<RackResponse>builder()
                .message("Tạo giá đỡ thành công")
                .result(response)
                .build();
    }

    /**
     * API cập nhật thông tin của giá đỡ theo ID.
     * Endpoint này cho phép người dùng cập nhật thông tin của một giá đỡ đã tồn tại.
     *
     * @param id ID của giá đỡ cần cập nhật.
     * @param request Thông tin cập nhật cho giá đỡ.
     * @return ApiResponse chứa thông tin của giá đỡ sau khi cập nhật.
     */
    @PutMapping("/{id}")
    public ApiResponse<RackResponse> updateRack(
            @PathVariable Long id,
            @Valid @RequestBody RackRequest request) {
        RackResponse response = rackService.updateRack(id, request);
        return ApiResponse.<RackResponse>builder()
                .message("Cập nhật giá đỡ thành công")
                .result(response)
                .build();
    }

    /**
     * API lấy thông tin của giá đỡ theo ID.
     * Endpoint này cho phép người dùng lấy thông tin chi tiết của một giá đỡ dựa trên ID.
     *
     * @param id ID của giá đỡ cần lấy.
     * @return ApiResponse chứa thông tin của giá đỡ.
     */
    @GetMapping("/{id}")
    public ApiResponse<RackResponse> getRackById(@PathVariable Long id) {
        RackResponse response = rackService.getRackById(id);
        return ApiResponse.<RackResponse>builder()
                .message("Lấy thông tin giá đỡ thành công")
                .result(response)
                .build();
    }

    /**
     * API lấy danh sách các giá đỡ với phân trang.
     * Endpoint này cho phép người dùng truy vấn danh sách các giá đỡ với phân trang.
     *
     * @param pageable Thông tin phân trang (số trang, kích thước trang).
     * @return ApiResponse chứa danh sách các giá đỡ theo phân trang.
     */
    @GetMapping
    public ApiResponse<PageDTO<RackResponse>> getAllRacks(Pageable pageable) {
        PageDTO<RackResponse> response = rackService.getAllRacks(pageable);
        return ApiResponse.<PageDTO<RackResponse>>builder()
                .message("Lấy danh sách giá đỡ thành công")
                .result(response)
                .build();
    }

    /**
     * API xóa giá đỡ theo ID.
     * Endpoint này cho phép người dùng xóa một giá đỡ dựa trên ID.
     *
     * @param id ID của giá đỡ cần xóa.
     * @return ApiResponse thông báo xóa thành công.
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRack(@PathVariable Long id) {
        rackService.deleteRack(id);
        return ApiResponse.<Void>builder()
                .message("Xóa giá đỡ thành công")
                .build();
    }
}
