package com.spkt.librasys.service.impl;

import com.spkt.librasys.dto.request.review.ReviewCreateRequest;
import com.spkt.librasys.dto.request.review.ReviewUpdateRequest;
import com.spkt.librasys.dto.response.review.ReviewResponse;
import com.spkt.librasys.entity.LoanTransaction;
import com.spkt.librasys.entity.Review;
import com.spkt.librasys.entity.User;
import com.spkt.librasys.entity.Document;
import com.spkt.librasys.exception.AppException;
import com.spkt.librasys.exception.ErrorCode;
import com.spkt.librasys.mapper.ReviewMapper;
import com.spkt.librasys.repository.LoanTransactionRepository;
import com.spkt.librasys.repository.ReviewRepository;
import com.spkt.librasys.repository.access.UserRepository;
import com.spkt.librasys.repository.DocumentRepository;
import com.spkt.librasys.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewServiceImpl implements ReviewService {

    ReviewRepository reviewRepository;
    UserRepository userRepository;
    DocumentRepository documentRepository;
    LoanTransactionRepository loanTransactionRepository;
    ReviewMapper reviewMapper;

    @Override
    @Transactional
    public ReviewResponse createReview(String userId, ReviewCreateRequest request) {
        Long documentId = Long.valueOf(request.getDocumentId());

        if (reviewRepository.existsByUserUserIdAndDocumentDocumentId(userId, documentId)) {
            throw new AppException(ErrorCode.REVIEW_ALREADY_EXISTS, "You have already reviewed this document.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "User not found"));

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND, "Document not found"));

        // Define acceptable statuses
        List<LoanTransaction.Status> acceptableStatuses = Arrays.asList(
                LoanTransaction.Status.RECEIVED,
                LoanTransaction.Status.RETURN_REQUESTED,
                LoanTransaction.Status.RETURNED
        );

        // Check if the user has borrowed the document with an acceptable status
        boolean hasBorrowed = loanTransactionRepository.existsByUserAndDocumentAndStatusIn(user, document, acceptableStatuses);
        if (!hasBorrowed) {
            throw new AppException(ErrorCode.USER_HAS_NOT_BORROWED_DOCUMENT, "You can only review documents you have borrowed.");
        }
        // Use mapper to convert DTO to entity
        Review review = reviewMapper.toReview(request);
        review.setUser(user);
        review.setDocument(document);
        review.setStatus(Review.Status.APPROVED); // Or PENDING based on your configuration
        review.setCreatedAt(LocalDateTime.now());

        Review savedReview = reviewRepository.save(review);

        return reviewMapper.toReviewResponse(savedReview);
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(Long reviewId, String userId, ReviewUpdateRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND, "Review not found"));

        if (!review.getUser().getUserId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED, "You are not authorized to update this review.");
        }

        // Use mapper to update entity
        reviewMapper.updateReview(review, request);

        Review updatedReview = reviewRepository.save(review);

        return reviewMapper.toReviewResponse(updatedReview);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId, String userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND, "Review not found"));

        if (!review.getUser().getUserId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED, "You are not authorized to delete this review.");
        }

        reviewRepository.delete(review);
    }

    @Override
    public Page<ReviewResponse> getReviewsByDocument(Long documentId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByDocumentDocumentIdAndStatus(
                documentId, Review.Status.APPROVED, pageable);

        return reviews.map(reviewMapper::toReviewResponse);
    }

    @Override
    public Page<ReviewResponse> getUserReviews(String userId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByUserUserId(userId, pageable);

        return reviews.map(reviewMapper::toReviewResponse);
    }

    @Override
    public ReviewResponse getReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND, "Review not found"));

        return reviewMapper.toReviewResponse(review);
    }

    @Override
    @Transactional
    public void approveReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND, "Review not found"));

        review.setStatus(Review.Status.APPROVED);
        reviewRepository.save(review);
    }

    @Override
    @Transactional
    public void rejectReview(Long reviewId, String rejectionReason) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND, "Review not found"));

        review.setStatus(Review.Status.REJECTED);
        // Optionally, store the rejection reason if you have a field for it
        reviewRepository.save(review);
    }

    @Override
    public boolean hasUserReviewedDocument(String userId, Long documentId) {
        return reviewRepository.existsByUserUserIdAndDocumentDocumentId(userId, documentId);
    }
}
