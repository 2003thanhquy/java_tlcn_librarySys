package com.spkt.librasys.repository.document;

import com.spkt.librasys.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> , JpaSpecificationExecutor<Document> {
//    Page<Document> findAll(Pageable pageable);
//    List<Document> findByDocumentNameContainingOrAuthorContaining(String documentName, String author);
}