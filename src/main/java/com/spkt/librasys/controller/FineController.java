package com.spkt.librasys.controller;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.response.ApiResponse;
import com.spkt.librasys.dto.response.fine.FineResponse;
import com.spkt.librasys.service.FineService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/**
 * Lớp Controller quản lý các yêu cầu liên quan đến các khoản phạt.
 * Cung cấp các endpoint cho việc truy xuất danh sách các khoản phạt và thanh toán các khoản phạt.
 */
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/fines")
public class FineController {

    FineService fineService;

    /**
     * Truy xuất danh sách tất cả các khoản phạt với phân trang.
     * Endpoint này cho phép người dùng lấy danh sách các khoản phạt đã áp dụng trong hệ thống.
     *
     * @param pageable Thông tin phân trang (số trang, kích thước trang, hướng sắp xếp).
     * @return ApiResponse chứa danh sách các khoản phạt theo phân trang.
     */
    @GetMapping
    public ApiResponse<PageDTO<FineResponse>> getAllFines(Pageable pageable) {
        Page<FineResponse> fines = fineService.getAllFines(pageable);
        PageDTO<FineResponse> pageDTO = new PageDTO<>(fines);
        return ApiResponse.<PageDTO<FineResponse>>builder()
                .message("Danh sách các khoản phạt đã được truy xuất thành công")
                .result(pageDTO)
                .build();
    }

    /**
     * Cho phép người dùng thanh toán một khoản phạt cụ thể.
     * Endpoint này sẽ kích hoạt quá trình thanh toán khoản phạt được xác định bởi ID của nó.
     *
     * @param fineId ID của khoản phạt cần thanh toán.
     * @return ApiResponse thông báo rằng khoản phạt đã được thanh toán thành công.
     */
    @PostMapping("/{fineId}/pay")
    public ApiResponse<Void> payFine(@PathVariable Long fineId) {
        fineService.payFine(fineId);
        return ApiResponse.<Void>builder()
                .message("Khoản phạt đã được thanh toán thành công")
                .build();
    }
}
