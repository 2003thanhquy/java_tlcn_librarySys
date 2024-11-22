package com.spkt.librasys.repository;

import com.spkt.librasys.entity.Course;
import com.spkt.librasys.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

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

    Page<Document> findByCoursesIn(Set<Course> courses, Pageable pageable);

    // Tổng số sách trong thư viện
    @Query("SELECT SUM(d.quantity) FROM documents_001 d")
    long countTotalDocuments();

    // Tổng số sách còn sẵn
    @Query("SELECT SUM(d.availableCount) FROM documents_001 d")
    long countAvailableBooks();

    // Tổng số sách đã bị hư hỏng hoặc mất
    @Query("SELECT COUNT(d) FROM documents_001 d WHERE d.status = 'DISABLED'")
    long countDisabledDocuments();

    // Số sách theo từng thể loại
    @Query("SELECT dt.typeName, COUNT(d) FROM documents_001 d " +
            "JOIN d.documentTypes dt " +
            "GROUP BY dt.typeName")
    List<Object[]> countDocumentsByType();

    // Số sách theo danh sách code môn mở lớp trong năm
    @Query("SELECT pc.year, c.courseCode, COUNT(d) FROM documents_001 d " +
            "JOIN d.courses c " +
            "JOIN c.programClasses pc " +
            "WHERE pc.year = :year " +
            "GROUP BY pc.year, c.courseCode")
    List<Object[]> countDocumentsByCourseCode(@Param("year") String year);

}