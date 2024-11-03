package com.spkt.librasys.repository;

import com.spkt.librasys.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> , JpaSpecificationExecutor<Document> {
//    Page<Document> findAll(Pageable pageable);
//    List<Document> findByDocumentNameContainingOrAuthorContaining(String documentName, String author);
    //dashboard
//    @Query("SELECT d FROM documents_001 d JOIN loan_transaction_001 lt ON lt.document.documentId = d.documentId " +
//            "GROUP BY d.documentId ORDER BY COUNT(lt) DESC")
//    Page<Document> findTopBorrowedDocuments(Pageable pageable);

    @Query("SELECT d.documentName, COUNT(lt) as borrowCount " +
            "FROM documents_001 d JOIN loan_transaction_001 lt ON lt.document.documentId = d.documentId " +
            "GROUP BY d.documentId ORDER BY COUNT(lt) DESC")
    Page<Object[]> findTopBorrowedDocuments(Pageable pageable);

}