package com.spkt.librasys.service;
import com.spkt.librasys.dto.request.document.DocumentCreateRequest;
import com.spkt.librasys.dto.request.document.DocumentUpdateRequest;
import com.spkt.librasys.dto.response.document.DocumentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DocumentService {
    DocumentResponse createDocument(DocumentCreateRequest request);
    DocumentResponse updateDocument(Long id, DocumentUpdateRequest request);
    DocumentResponse getDocumentById(Long id);
    Page<DocumentResponse> getAllDocuments(String title, String author, String publisher, Long documentTypeId, Pageable pageable); // Cập nhật để hỗ trợ tìm kiếm nâng cao

    void deleteDocument(Long id);
}
