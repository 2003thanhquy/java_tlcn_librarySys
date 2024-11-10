package com.spkt.librasys.controller;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.response.ApiResponse;
import com.spkt.librasys.dto.response.document.DocumentResponse;
import com.spkt.librasys.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    /**
     * Điểm cuối API để lấy các Document được đề xuất cho người dùng hiện tại với phân trang.
     *
     * @param page Số trang (mặc định là 0).
     * @param size Kích thước trang (mặc định là 10).
     * @return ResponseEntity chứa PageDTO với danh sách các Document được đề xuất.
     */
    @GetMapping("/recommendations/documents")
    public ApiResponse<PageDTO<DocumentResponse>> getUserDocumentRecommendations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        PageDTO<DocumentResponse> recommendations = recommendationService.getRecommendedDocumentsForCurrentUser(pageable);

        return  ApiResponse.<PageDTO<DocumentResponse>>builder()
                .result(recommendations)
                .message("Lấy đề xuất tài liệu thành công")
                .build();

    }
}
