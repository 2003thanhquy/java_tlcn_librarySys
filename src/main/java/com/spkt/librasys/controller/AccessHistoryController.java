package com.spkt.librasys.controller;

import com.spkt.librasys.dto.response.accessHistoryResponse.AccessHistoryResponse;
import com.spkt.librasys.dto.response.ApiResponse;
import com.spkt.librasys.service.AccessHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/access-histories")
@RequiredArgsConstructor
public class AccessHistoryController {

    private final AccessHistoryService accessHistoryService;

    // Lấy tất cả lịch sử truy cập có phân trang
    @GetMapping
    public ApiResponse<Page<AccessHistoryResponse>> getAllAccessHistories(Pageable pageable) {
        Page<AccessHistoryResponse> responseList = accessHistoryService.getAllAccessHistories(pageable);
        return ApiResponse.<Page<AccessHistoryResponse>>builder()
                .message("Access histories retrieved successfully")
                .result(responseList)
                .build();
    }

    // Lấy thông tin lịch sử truy cập theo ID
    @GetMapping("/{id}")
    public ApiResponse<AccessHistoryResponse> getAccessHistoryById(@PathVariable Long id) {
        AccessHistoryResponse response = accessHistoryService.getAccessHistoryById(id);
        return ApiResponse.<AccessHistoryResponse>builder()
                .message("Access history retrieved successfully")
                .result(response)
                .build();
    }

    // Tìm kiếm nâng cao lịch sử truy cập (có phân trang)
    @GetMapping("/search")
    public ApiResponse<Page<AccessHistoryResponse>> searchAccessHistories(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) Long documentId,
            @RequestParam(required = false) String activity,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            Pageable pageable) {
        Page<AccessHistoryResponse> responseList = accessHistoryService.searchAccessHistories(userId, documentId, activity, fromDate, toDate, pageable);
        return ApiResponse.<Page<AccessHistoryResponse>>builder()
                .message("Access histories retrieved successfully")
                .result(responseList)
                .build();
    }

    // Xóa lịch sử truy cập theo ID
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAccessHistory(@PathVariable Long id) {
        accessHistoryService.deleteAccessHistoryById(id);
        return ApiResponse.<Void>builder()
                .message("Access history deleted successfully")
                .build();
    }
}
