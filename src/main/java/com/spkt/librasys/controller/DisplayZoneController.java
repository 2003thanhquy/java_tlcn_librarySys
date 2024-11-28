package com.spkt.librasys.controller;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.request.displayzone.DisplayZoneRequest;
import com.spkt.librasys.dto.response.ApiResponse;
import com.spkt.librasys.dto.response.displayzone.DisplayZoneResponse;
import com.spkt.librasys.service.DisplayZoneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/**
 * Controller xử lý các yêu cầu liên quan đến DisplayZone.
 * Các API này cho phép tạo, sửa, xóa và lấy thông tin về DisplayZone.
 */
@RestController
@RequestMapping("/api/v1/display-zones")
@RequiredArgsConstructor
public class DisplayZoneController {

    private final DisplayZoneService displayZoneService;

    /**
     * Tạo một DisplayZone mới.
     *
     * @param request Dữ liệu yêu cầu để tạo DisplayZone.
     * @return ApiResponse chứa thông tin của DisplayZone vừa tạo.
     */
    @PostMapping
    public ApiResponse<DisplayZoneResponse> createDisplayZone(@Valid @RequestBody DisplayZoneRequest request) {
        DisplayZoneResponse response = displayZoneService.createDisplayZone(request);
        return ApiResponse.<DisplayZoneResponse>builder()
                .message("DisplayZone created successfully")
                .result(response)
                .build();
    }

    /**
     * Cập nhật thông tin của DisplayZone theo ID.
     *
     * @param id ID của DisplayZone cần cập nhật.
     * @param request Dữ liệu yêu cầu để cập nhật DisplayZone.
     * @return ApiResponse chứa thông tin của DisplayZone sau khi cập nhật.
     */
    @PutMapping("/{id}")
    public ApiResponse<DisplayZoneResponse> updateDisplayZone(
            @PathVariable Long id,
            @Valid @RequestBody DisplayZoneRequest request) {
        DisplayZoneResponse response = displayZoneService.updateDisplayZone(id, request);
        return ApiResponse.<DisplayZoneResponse>builder()
                .message("DisplayZone updated successfully")
                .result(response)
                .build();
    }

    /**
     * Lấy thông tin của DisplayZone theo ID.
     *
     * @param id ID của DisplayZone cần lấy thông tin.
     * @return ApiResponse chứa thông tin của DisplayZone tìm thấy.
     */
    @GetMapping("/{id}")
    public ApiResponse<DisplayZoneResponse> getDisplayZoneById(@PathVariable Long id) {
        DisplayZoneResponse response = displayZoneService.getDisplayZoneById(id);
        return ApiResponse.<DisplayZoneResponse>builder()
                .message("DisplayZone retrieved successfully")
                .result(response)
                .build();
    }

    /**
     * Lấy danh sách tất cả DisplayZone với phân trang.
     *
     * @param pageable Tham số phân trang để giới hạn kết quả trả về.
     * @return ApiResponse chứa danh sách các DisplayZone.
     */
    @GetMapping
    public ApiResponse<PageDTO<DisplayZoneResponse>> getAllDisplayZones(Pageable pageable) {
        PageDTO<DisplayZoneResponse> response = displayZoneService.getAllDisplayZones(pageable);
        return ApiResponse.<PageDTO<DisplayZoneResponse>>builder()
                .message("DisplayZones retrieved successfully")
                .result(response)
                .build();
    }

    /**
     * Xóa DisplayZone theo ID.
     *
     * @param id ID của DisplayZone cần xóa.
     * @return ApiResponse trả về thông báo thành công khi xóa.
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteDisplayZone(@PathVariable Long id) {
        displayZoneService.deleteDisplayZone(id);
        return ApiResponse.<Void>builder()
                .message("DisplayZone deleted successfully")
                .build();
    }
}
