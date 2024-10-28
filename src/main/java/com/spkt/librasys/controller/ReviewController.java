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

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewController {

    ReviewService reviewService;

    // Create a new review
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<ReviewResponse> createReview(
            @Valid @RequestBody ReviewCreateRequest request,
            @RequestParam String userId) {

        ReviewResponse reviewResponse = reviewService.createReview(userId, request);
        return ApiResponse.<ReviewResponse>builder()
                .message("Review created successfully")
                .result(reviewResponse)
                .build();
    }

    // Update an existing review
    @PutMapping("/{reviewId}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<ReviewResponse> updateReview(
            @PathVariable Long reviewId,
            @RequestParam String userId,
            @Valid @RequestBody ReviewUpdateRequest request) {

        ReviewResponse reviewResponse = reviewService.updateReview(reviewId, userId, request);
        return ApiResponse.<ReviewResponse>builder()
                .message("Review updated successfully")
                .result(reviewResponse)
                .build();
    }

    // Delete a review
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<Void> deleteReview(
            @PathVariable Long reviewId,
            @RequestParam String userId) {

        reviewService.deleteReview(reviewId, userId);
        return ApiResponse.<Void>builder()
                .message("Review deleted successfully")
                .build();
    }

    // Get reviews for a document
    @GetMapping("/document/{documentId}")
    public ApiResponse<PageDTO<ReviewResponse>> getReviewsByDocument(
            @PathVariable Long documentId,
            Pageable pageable) {

        Page<ReviewResponse> reviews = reviewService.getReviewsByDocument(documentId, pageable);
        PageDTO<ReviewResponse> pageDTO = new PageDTO<>(reviews);
        return ApiResponse.<PageDTO<ReviewResponse>>builder()
                .message("Reviews retrieved successfully")
                .result(pageDTO)
                .build();
    }

    // Get reviews by a user
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<PageDTO<ReviewResponse>> getUserReviews(
            @PathVariable String userId,
            Pageable pageable) {

        Page<ReviewResponse> reviews = reviewService.getUserReviews(userId, pageable);
        PageDTO<ReviewResponse> pageDTO = new PageDTO<>(reviews);
        return ApiResponse.<PageDTO<ReviewResponse>>builder()
                .message("User reviews retrieved successfully")
                .result(pageDTO)
                .build();
    }

    // Admin approves a review
    @PostMapping("/{reviewId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> approveReview(@PathVariable Long reviewId) {
        reviewService.approveReview(reviewId);
        return ApiResponse.<Void>builder()
                .message("Review approved successfully")
                .build();
    }

    // Admin rejects a review
    @PostMapping("/{reviewId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> rejectReview(
            @PathVariable Long reviewId,
            @RequestBody String rejectionReason) {

        reviewService.rejectReview(reviewId, rejectionReason);
        return ApiResponse.<Void>builder()
                .message("Review rejected successfully")
                .build();
    }
}
