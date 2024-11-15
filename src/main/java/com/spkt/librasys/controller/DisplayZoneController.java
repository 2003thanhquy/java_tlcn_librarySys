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

@RestController
@RequestMapping("/api/v1/display-zones")
@RequiredArgsConstructor
public class DisplayZoneController {

    private final DisplayZoneService displayZoneService;

    @PostMapping
    public ApiResponse<DisplayZoneResponse> createDisplayZone(@Valid @RequestBody DisplayZoneRequest request) {
        DisplayZoneResponse response = displayZoneService.createDisplayZone(request);
        return ApiResponse.<DisplayZoneResponse>builder()
                .message("DisplayZone created successfully")
                .result(response)
                .build();
    }

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

    @GetMapping("/{id}")
    public ApiResponse<DisplayZoneResponse> getDisplayZoneById(@PathVariable Long id) {
        DisplayZoneResponse response = displayZoneService.getDisplayZoneById(id);
        return ApiResponse.<DisplayZoneResponse>builder()
                .message("DisplayZone retrieved successfully")
                .result(response)
                .build();
    }

    @GetMapping
    public ApiResponse<PageDTO<DisplayZoneResponse>> getAllDisplayZones(Pageable pageable) {
        PageDTO<DisplayZoneResponse> response = displayZoneService.getAllDisplayZones(pageable);
        return ApiResponse.<PageDTO<DisplayZoneResponse>>builder()
                .message("DisplayZones retrieved successfully")
                .result(response)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteDisplayZone(@PathVariable Long id) {
        displayZoneService.deleteDisplayZone(id);
        return ApiResponse.<Void>builder()
                .message("DisplayZone deleted successfully")
                .build();
    }
}
