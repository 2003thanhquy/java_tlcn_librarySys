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

@RestController
@RequestMapping("/api/v1/racks")
@RequiredArgsConstructor
public class RackController {

    private final RackService rackService;

    @PostMapping
    public ApiResponse<RackResponse> createRack(@Valid @RequestBody RackRequest request) {
        RackResponse response = rackService.createRack(request);
        return ApiResponse.<RackResponse>builder()
                .message("Rack created successfully")
                .result(response)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<RackResponse> updateRack(
            @PathVariable Long id,
            @Valid @RequestBody RackRequest request) {
        RackResponse response = rackService.updateRack(id, request);
        return ApiResponse.<RackResponse>builder()
                .message("Rack updated successfully")
                .result(response)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<RackResponse> getRackById(@PathVariable Long id) {
        RackResponse response = rackService.getRackById(id);
        return ApiResponse.<RackResponse>builder()
                .message("Rack retrieved successfully")
                .result(response)
                .build();
    }

    @GetMapping
    public ApiResponse<PageDTO<RackResponse>> getAllRacks(Pageable pageable) {
        PageDTO<RackResponse> response = rackService.getAllRacks(pageable);
        return ApiResponse.<PageDTO<RackResponse>>builder()
                .message("Racks retrieved successfully")
                .result(response)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRack(@PathVariable Long id) {
        rackService.deleteRack(id);
        return ApiResponse.<Void>builder()
                .message("Rack deleted successfully")
                .build();
    }
}
