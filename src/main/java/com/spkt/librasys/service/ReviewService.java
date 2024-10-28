package com.spkt.librasys.service;

import com.spkt.librasys.dto.request.review.ReviewCreateRequest;
import com.spkt.librasys.dto.request.review.ReviewUpdateRequest;
import com.spkt.librasys.dto.response.review.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {

    ReviewResponse createReview(String userId, ReviewCreateRequest request);

    ReviewResponse updateReview(Long reviewId, String userId, ReviewUpdateRequest request);

    void deleteReview(Long reviewId, String userId);

    Page<ReviewResponse> getReviewsByDocument(Long documentId, Pageable pageable);

    Page<ReviewResponse> getUserReviews(String userId, Pageable pageable);

    ReviewResponse getReview(Long reviewId);

    void approveReview(Long reviewId);

    void rejectReview(Long reviewId, String rejectionReason);

    boolean hasUserReviewedDocument(String userId, Long documentId);
}
