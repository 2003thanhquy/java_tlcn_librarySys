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

/**
 * Lớp Controller xử lý các yêu cầu liên quan đến các tài liệu được đề xuất cho người dùng.
 * Cung cấp API để lấy các tài liệu được đề xuất dựa trên các tiêu chí của người dùng.
 */
@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    /**
     * API để lấy các tài liệu được đề xuất cho người dùng hiện tại với phân trang.
     * Endpoint này trả về một danh sách tài liệu được đề xuất cho người dùng với phân trang.
     *
     * @param page Số trang, mặc định là 0 nếu không được cung cấp.
     * @param size Kích thước trang, mặc định là 10 nếu không được cung cấp.
     * @return ApiResponse chứa danh sách các tài liệu đề xuất.
     */
    @GetMapping("/users")
    public ApiResponse<PageDTO<DocumentResponse>> getUserDocumentRecommendations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Tạo đối tượng Pageable từ các tham số page và size
        Pageable pageable = PageRequest.of(page, size);

        // Lấy các tài liệu đề xuất cho người dùng hiện tại
        PageDTO<DocumentResponse> recommendations = recommendationService.getRecommendedDocumentsForCurrentUser(pageable);

        return  ApiResponse.<PageDTO<DocumentResponse>>builder()
                .result(recommendations)
                .message("Lấy đề xuất tài liệu thành công")
                .build();
    }
}
