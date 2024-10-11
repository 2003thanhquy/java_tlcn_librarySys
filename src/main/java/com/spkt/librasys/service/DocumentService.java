package com.spkt.librasys.service;
import com.spkt.librasys.dto.request.documentRequest.DocumentCreateRequest;
import com.spkt.librasys.dto.request.documentRequest.DocumentUpdateRequest;
import com.spkt.librasys.dto.response.documentResponse.DocumentResponse;
import com.spkt.librasys.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
public interface DocumentService {
    DocumentResponse createDocument(DocumentCreateRequest request);
    DocumentResponse updateDocument(Long id, DocumentUpdateRequest request);
    DocumentResponse getDocumentById(Long id);
    Page<DocumentResponse> getAllDocuments(String title, String author, String publisher, Long documentTypeId, Pageable pageable); // Cập nhật để hỗ trợ tìm kiếm nâng cao

    void deleteDocument(Long id);
}
