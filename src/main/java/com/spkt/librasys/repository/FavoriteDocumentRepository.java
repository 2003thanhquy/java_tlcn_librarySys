package com.spkt.librasys.repository;

import com.spkt.librasys.entity.Document;
import com.spkt.librasys.entity.FavoriteDocument;
import com.spkt.librasys.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteDocumentRepository extends JpaRepository<FavoriteDocument, Long> {
    Page<FavoriteDocument> findAllByUser(User user, Pageable pageable);
    void deleteByUserAndDocument(User user, Document document);
    boolean existsByUserAndDocument(User user, Document document);

    // Phương thức tìm kiếm bản ghi yêu thích theo người dùng và tài liệu
    Optional<FavoriteDocument> findByUserAndDocument(User user, Document document);


}