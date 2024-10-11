package com.spkt.librasys.service.impl;

import com.spkt.librasys.dto.request.documentRequest.DocumentCreateRequest;
import com.spkt.librasys.dto.request.documentRequest.DocumentUpdateRequest;
import com.spkt.librasys.dto.response.documentResponse.DocumentResponse;
import com.spkt.librasys.entity.Document;
import com.spkt.librasys.entity.DocumentType;
import com.spkt.librasys.exception.AppException;
import com.spkt.librasys.exception.ErrorCode;
import com.spkt.librasys.mapper.DocumentMapper;
import com.spkt.librasys.repository.document.DocumentRepository;
import com.spkt.librasys.repository.document.DocumentTypeRepository;
import com.spkt.librasys.service.DocumentService;
import com.spkt.librasys.specification.DocumentSpecification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DocumentServiceImpl implements DocumentService {

    DocumentRepository documentRepository;
    DocumentTypeRepository documentTypeRepository;
    DocumentMapper documentMapper;

    @Override
    public DocumentResponse createDocument(DocumentCreateRequest request) {
        // Kiểm tra xem loại tài liệu có tồn tại hay không
        DocumentType documentType = documentTypeRepository.findById(request.getDocumentTypeId())
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));

        // Sử dụng mapper để chuyển đổi DTO sang entity
        Document document = documentMapper.toDocument(request);
        document.setDocumentType(documentType);

        Document savedDocument = documentRepository.save(document);
        return documentMapper.toDocumentResponse(savedDocument);
    }

    @Override
    public DocumentResponse updateDocument(Long id, DocumentUpdateRequest request) {
        // Kiểm tra xem tài liệu có tồn tại không
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));

        // Kiểm tra xem loại tài liệu có tồn tại không
        DocumentType documentType = documentTypeRepository.findById(request.getDocumentTypeId())
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));

        // Sử dụng mapper để cập nhật thông tin từ DTO
        documentMapper.updateDocument(document, request);
        document.setDocumentType(documentType);

        Document updatedDocument = documentRepository.save(document);
        return documentMapper.toDocumentResponse(updatedDocument);
    }

    @Override
    public DocumentResponse getDocumentById(Long id) {
        // Kiểm tra xem tài liệu có tồn tại không
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));

        return documentMapper.toDocumentResponse(document);
    }

    @Override
    public Page<DocumentResponse> getAllDocuments(String title, String author, String publisher, Long documentTypeId, Pageable pageable) {

        //api/v1/documents?page=1&size=5&sort=documentName,asc
        Specification<Document> spec = Specification
                .where(DocumentSpecification.hasTitle(title))
                .and(DocumentSpecification.hasAuthor(author))
                .and(DocumentSpecification.hasPublisher(publisher))
                .and(DocumentSpecification.hasDocumentTypeId(documentTypeId));

        return documentRepository.findAll(spec, pageable)
                .map(documentMapper::toDocumentResponse);
    }



    @Override
    public void deleteDocument(Long id) {
        // Kiểm tra xem tài liệu có tồn tại không trước khi xóa
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));

        documentRepository.delete(document);
    }
}
