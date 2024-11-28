package com.spkt.librasys.controller;

import com.spkt.librasys.dto.request.review.ReviewCreateRequest;
import com.spkt.librasys.dto.request.review.ReviewUpdateRequest;
import com.spkt.librasys.dto.response.ApiResponse;
import com.spkt.librasys.dto.response.review.ReviewResponse;
import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Lớp Controller xử lý các yêu cầu liên quan đến đánh giá tài liệu.
 * Cung cấp các API để tạo, sửa, xóa và lấy thông tin đánh giá.
 */
@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewController {

    ReviewService reviewService;

    /**
     * Tạo mới một đánh giá cho tài liệu.
     *
     * @param request yêu cầu tạo đánh giá
     * @param userId ID của người dùng thực hiện đánh giá
     * @return ApiResponse chứa thông tin của đánh giá mới tạo
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<ReviewResponse> createReview(
            @Valid @RequestBody ReviewCreateRequest request,
            @RequestParam String userId) {

        ReviewResponse reviewResponse = reviewService.createReview(userId, request);
        return ApiResponse.<ReviewResponse>builder()
                .message("Tạo đánh giá thành công")
                .result(reviewResponse)
                .build();
    }

    /**
     * Cập nhật đánh giá đã tồn tại.
     *
     * @param reviewId ID của đánh giá cần cập nhật
     * @param userId ID của người dùng thực hiện cập nhật
     * @param request yêu cầu cập nhật đánh giá
     * @return ApiResponse chứa thông tin của đánh giá sau khi cập nhật
     */
    @PutMapping("/{reviewId}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<ReviewResponse> updateReview(
            @PathVariable Long reviewId,
            @RequestParam String userId,
            @Valid @RequestBody ReviewUpdateRequest request) {

        ReviewResponse reviewResponse = reviewService.updateReview(reviewId, userId, request);
        return ApiResponse.<ReviewResponse>builder()
                .message("Cập nhật đánh giá thành công")
                .result(reviewResponse)
                .build();
    }

    /**
     * Xóa một đánh giá.
     *
     * @param reviewId ID của đánh giá cần xóa
     * @param userId ID của người dùng thực hiện xóa
     * @return ApiResponse thông báo xóa đánh giá thành công
     */
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<Void> deleteReview(
            @PathVariable Long reviewId,
            @RequestParam String userId) {

        reviewService.deleteReview(reviewId, userId);
        return ApiResponse.<Void>builder()
                .message("Xóa đánh giá thành công")
                .build();
    }

    /**
     * Lấy danh sách đánh giá của một tài liệu.
     *
     * @param documentId ID của tài liệu cần lấy đánh giá
     * @param pageable thông tin phân trang
     * @return ApiResponse chứa danh sách đánh giá của tài liệu
     */
    @GetMapping("/document/{documentId}")
    public ApiResponse<PageDTO<ReviewResponse>> getReviewsByDocument(
            @PathVariable Long documentId,
            Pageable pageable) {

        Page<ReviewResponse> reviews = reviewService.getReviewsByDocument(documentId, pageable);
        PageDTO<ReviewResponse> pageDTO = new PageDTO<>(reviews);
        return ApiResponse.<PageDTO<ReviewResponse>>builder()
                .message("Lấy danh sách đánh giá thành công")
                .result(pageDTO)
                .build();
    }

    /**
     * Lấy danh sách đánh giá của một người dùng.
     *
     * @param userId ID của người dùng cần lấy đánh giá
     * @param pageable thông tin phân trang
     * @return ApiResponse chứa danh sách đánh giá của người dùng
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<PageDTO<ReviewResponse>> getUserReviews(
            @PathVariable String userId,
            Pageable pageable) {

        Page<ReviewResponse> reviews = reviewService.getUserReviews(userId, pageable);
        PageDTO<ReviewResponse> pageDTO = new PageDTO<>(reviews);
        return ApiResponse.<PageDTO<ReviewResponse>>builder()
                .message("Lấy danh sách đánh giá của người dùng thành công")
                .result(pageDTO)
                .build();
    }

    /**
     * Duyệt một đánh giá bởi quản trị viên.
     *
     * @param reviewId ID của đánh giá cần duyệt
     * @return ApiResponse thông báo duyệt đánh giá thành công
     */
    @PostMapping("/{reviewId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> approveReview(@PathVariable Long reviewId) {
        reviewService.approveReview(reviewId);
        return ApiResponse.<Void>builder()
                .message("Duyệt đánh giá thành công")
                .build();
    }

    /**
     * Từ chối một đánh giá bởi quản trị viên.
     *
     * @param reviewId ID của đánh giá cần từ chối
     * @param rejectionReason lý do từ chối
     * @return ApiResponse thông báo từ chối đánh giá thành công
     */
    @PostMapping("/{reviewId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> rejectReview(
            @PathVariable Long reviewId,
            @RequestBody String rejectionReason) {

        reviewService.rejectReview(reviewId, rejectionReason);
        return ApiResponse.<Void>builder()
                .message("Từ chối đánh giá thành công")
                .build();
    }
}
