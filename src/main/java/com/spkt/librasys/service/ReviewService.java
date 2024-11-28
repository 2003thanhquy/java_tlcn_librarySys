package com.spkt.librasys.service;

import com.spkt.librasys.dto.request.review.ReviewCreateRequest;
import com.spkt.librasys.dto.request.review.ReviewUpdateRequest;
import com.spkt.librasys.dto.response.review.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Giao diện ReviewService định nghĩa các hành vi liên quan đến việc tạo, cập nhật, xóa và quản lý đánh giá tài liệu.
 */
public interface ReviewService {

    /**
     * Tạo một đánh giá mới cho tài liệu.
     *
     * @param userId ID của người dùng thực hiện đánh giá.
     * @param request Dữ liệu yêu cầu tạo đánh giá.
     * @return ReviewResponse chứa thông tin đánh giá vừa tạo.
     */
    ReviewResponse createReview(String userId, ReviewCreateRequest request);

    /**
     * Cập nhật thông tin đánh giá.
     *
     * @param reviewId ID của đánh giá cần cập nhật.
     * @param userId ID của người dùng thực hiện cập nhật.
     * @param request Dữ liệu yêu cầu cập nhật đánh giá.
     * @return ReviewResponse chứa thông tin đánh giá sau khi cập nhật.
     */
    ReviewResponse updateReview(Long reviewId, String userId, ReviewUpdateRequest request);

    /**
     * Xóa một đánh giá.
     *
     * @param reviewId ID của đánh giá cần xóa.
     * @param userId ID của người dùng thực hiện việc xóa.
     */
    void deleteReview(Long reviewId, String userId);

    /**
     * Lấy danh sách các đánh giá của một tài liệu.
     *
     * @param documentId ID của tài liệu cần lấy đánh giá.
     * @param pageable Thông tin phân trang.
     * @return Page chứa danh sách đánh giá cho tài liệu đó.
     */
    Page<ReviewResponse> getReviewsByDocument(Long documentId, Pageable pageable);

    /**
     * Lấy danh sách các đánh giá của người dùng.
     *
     * @param userId ID của người dùng cần lấy đánh giá.
     * @param pageable Thông tin phân trang.
     * @return Page chứa danh sách các đánh giá của người dùng đó.
     */
    Page<ReviewResponse> getUserReviews(String userId, Pageable pageable);

    /**
     * Lấy thông tin chi tiết của một đánh giá.
     *
     * @param reviewId ID của đánh giá cần lấy.
     * @return ReviewResponse chứa thông tin đánh giá.
     */
    ReviewResponse getReview(Long reviewId);

    /**
     * Phê duyệt một đánh giá.
     *
     * @param reviewId ID của đánh giá cần phê duyệt.
     */
    void approveReview(Long reviewId);

    /**
     * Từ chối một đánh giá và cung cấp lý do từ chối.
     *
     * @param reviewId ID của đánh giá cần từ chối.
     * @param rejectionReason Lý do từ chối đánh giá.
     */
    void rejectReview(Long reviewId, String rejectionReason);

    /**
     * Kiểm tra xem người dùng đã đánh giá tài liệu hay chưa.
     *
     * @param userId ID của người dùng cần kiểm tra.
     * @param documentId ID của tài liệu cần kiểm tra.
     * @return true nếu người dùng đã đánh giá tài liệu, false nếu không.
     */
    boolean hasUserReviewedDocument(String userId, Long documentId);
}
