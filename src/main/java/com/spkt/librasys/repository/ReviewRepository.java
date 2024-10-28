package com.spkt.librasys.repository;

import com.spkt.librasys.entity.Review;
import com.spkt.librasys.entity.Review.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByUserUserIdAndDocumentDocumentId(String userId, Long documentId);

    Page<Review> findByDocumentDocumentIdAndStatus(Long documentId, Status status, Pageable pageable);

    Page<Review> findByUserUserId(String userId, Pageable pageable);
}
