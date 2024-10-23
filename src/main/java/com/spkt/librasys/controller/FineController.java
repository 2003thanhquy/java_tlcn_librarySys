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

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/fines")
public class FineController {

    FineService fineService;

    // Lấy danh sách tất cả các khoản phạt
    @GetMapping
    public ApiResponse<PageDTO<FineResponse>> getAllFines(Pageable pageable) {
        Page<FineResponse> fines = fineService.getAllFines(pageable);
        PageDTO<FineResponse> pageDTO = new PageDTO<>(fines);
        return ApiResponse.<PageDTO<FineResponse>>builder()
                .message("Fines retrieved successfully")
                .result(pageDTO)
                .build();
    }

    // Thanh toán khoản phạt
    @PostMapping("/{fineId}/pay")
    public ApiResponse<Void> payFine(@PathVariable Long fineId) {
        fineService.payFine(fineId);
        return ApiResponse.<Void>builder()
                .message("Fine paid successfully")
                .build();
    }
}