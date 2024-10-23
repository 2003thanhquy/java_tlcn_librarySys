package com.spkt.librasys.service;

import com.spkt.librasys.dto.request.document.DocumentTypeCreateRequest;
import com.spkt.librasys.dto.response.document.DocumentTypeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DocumentTypeService {
    Page<DocumentTypeResponse> getAllDocumentTypes(Pageable pageable);
    DocumentTypeResponse createDocumentType(DocumentTypeCreateRequest request);
    DocumentTypeResponse updateDocumentType(String typeName, DocumentTypeCreateRequest request);
    void deleteDocumentType(String typeName);
}