package com.spkt.librasys.service.impl;

import com.spkt.librasys.dto.request.document.DocumentTypeCreateRequest;
import com.spkt.librasys.dto.response.document.DocumentTypeResponse;
import com.spkt.librasys.entity.DocumentType;
import com.spkt.librasys.exception.AppException;
import com.spkt.librasys.exception.ErrorCode;
import com.spkt.librasys.mapper.DocumentTypeMapper;
import com.spkt.librasys.repository.document.DocumentTypeRepository;
import com.spkt.librasys.service.DocumentTypeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DocumentTypeServiceImpl implements DocumentTypeService {

    DocumentTypeRepository documentTypeRepository;
    DocumentTypeMapper documentTypeMapper;

    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public Page<DocumentTypeResponse> getAllDocumentTypes(Pageable pageable) {
        return documentTypeRepository.findAll(pageable)
                .map(documentTypeMapper::toDocumentTypeResponse);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public DocumentTypeResponse createDocumentType(DocumentTypeCreateRequest request) {
        DocumentType documentType = documentTypeMapper.toDocumentType(request);
        DocumentType savedDocumentType = documentTypeRepository.save(documentType);
        return documentTypeMapper.toDocumentTypeResponse(savedDocumentType);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public DocumentTypeResponse updateDocumentType(String typeName, DocumentTypeCreateRequest request) {
        DocumentType documentType = documentTypeRepository.findByTypeName(typeName)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_TYPE_NOT_FOUND));
        documentType.setTypeName(request.getTypeName());
        DocumentType updatedDocumentType = documentTypeRepository.save(documentType);
        return documentTypeMapper.toDocumentTypeResponse(updatedDocumentType);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteDocumentType(String typeName) {
        DocumentType documentType = documentTypeRepository.findByTypeName(typeName)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_TYPE_NOT_FOUND));
        documentTypeRepository.delete(documentType);
    }
}