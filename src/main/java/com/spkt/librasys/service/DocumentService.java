package com.spkt.librasys.service;
import com.spkt.librasys.dto.request.document.DocumentCreateRequest;
import com.spkt.librasys.dto.request.document.DocumentQuantityUpdateRequest;
import com.spkt.librasys.dto.request.document.DocumentSearchRequest;
import com.spkt.librasys.dto.request.document.DocumentUpdateRequest;
import com.spkt.librasys.dto.response.document.DocumentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DocumentService {
    DocumentResponse createDocument(DocumentCreateRequest request);
    DocumentResponse updateDocument(Long id, DocumentUpdateRequest request);
    DocumentResponse getDocumentById(Long id);
    Page<DocumentResponse> searchDocuments(DocumentSearchRequest searchRequest, Pageable pageable);
    Page<DocumentResponse> getAllDocuments(Pageable pageable);
    void deleteDocument(Long id);
    //
    Page<DocumentResponse> getFavoriteDocuments(Pageable pageable);
    void classifyDocument(Long id, String newTypeName);
    void favoriteDocument(Long id);
   // void downloadDocument(Long id);
    void unFavoriteDocument(Long documentId);
   boolean isFavoriteDocument(Long documentId);
    void deleteDocumentsByIds(List<Long> documentIds);

    void updateQuantity(DocumentQuantityUpdateRequest request);

}
