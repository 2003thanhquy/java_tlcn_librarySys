package com.spkt.librasys.service.impl;

import com.spkt.librasys.constant.DocumentActivityStatus;
import com.spkt.librasys.dto.request.documentRequest.DocumentCreateRequest;
import com.spkt.librasys.dto.request.documentRequest.DocumentUpdateRequest;
import com.spkt.librasys.dto.response.documentResponse.DocumentResponse;
import com.spkt.librasys.entity.Document;
import com.spkt.librasys.entity.DocumentType;
import com.spkt.librasys.entity.User;
import com.spkt.librasys.exception.AppException;
import com.spkt.librasys.exception.ErrorCode;
import com.spkt.librasys.mapper.DocumentMapper;
import com.spkt.librasys.repository.document.DocumentRepository;
import com.spkt.librasys.repository.document.DocumentTypeRepository;
import com.spkt.librasys.repository.access.UserRepository;
import com.spkt.librasys.service.DocumentService;
import com.spkt.librasys.specification.DocumentSpecification;
import com.spkt.librasys.service.impl.AccessHistoryServiceImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DocumentServiceImpl implements DocumentService {

    DocumentRepository documentRepository;
    DocumentTypeRepository documentTypeRepository;
    UserRepository userRepository;
    DocumentMapper documentMapper;
    AccessHistoryServiceImpl accessHistoryService;

    @Override
    public DocumentResponse createDocument(DocumentCreateRequest request) {
        DocumentType documentType = documentTypeRepository.findById(request.getDocumentTypeId())
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));

        Document document = documentMapper.toDocument(request);
        document.setDocumentType(documentType);

        Document savedDocument = documentRepository.save(document);

        // Ghi lại lịch sử tạo tài liệu
//        User currentUser = getCurrentUser();
//        accessHistoryService.recordAccess(currentUser, savedDocument, DocumentActivityStatus.CREATED);

        return documentMapper.toDocumentResponse(savedDocument);
    }

    @Override
    public DocumentResponse updateDocument(Long id, DocumentUpdateRequest request) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));

        DocumentType documentType = documentTypeRepository.findById(request.getDocumentTypeId())
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));

        documentMapper.updateDocument(document, request);
        document.setDocumentType(documentType);

        Document updatedDocument = documentRepository.save(document);

        // Ghi lại lịch sử cập nhật tài liệu
//        User currentUser = getCurrentUser();
//        accessHistoryService.recordAccess(currentUser, updatedDocument, DocumentActivityStatus.EDIT.getValue());

        return documentMapper.toDocumentResponse(updatedDocument);
    }

    @Override
    public DocumentResponse getDocumentById(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));

        // Ghi lại lịch sử đọc tài liệu
//        User currentUser = getCurrentUser();
//        accessHistoryService.recordAccess(currentUser, document, "Viewed Document");

        return documentMapper.toDocumentResponse(document);
    }

    @Override
    public Page<DocumentResponse> getAllDocuments(String title, String author, String publisher, Long documentTypeId, Pageable pageable) {
        Specification<Document> spec = Specification
                .where(DocumentSpecification.hasTitle(title))
                .and(DocumentSpecification.hasAuthor(author))
                .and(DocumentSpecification.hasPublisher(publisher))
                .and(DocumentSpecification.hasDocumentTypeId(documentTypeId));

        Page<Document> documents = documentRepository.findAll(spec, pageable);

        // Ghi lại lịch sử lấy tất cả tài liệu
//        User currentUser = getCurrentUser();
//        documents.forEach(document -> accessHistoryService.recordAccess(currentUser, document, "Listed Document"));

        return documents.map(documentMapper::toDocumentResponse);
    }

    @Override
    public void deleteDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));

        //documentRepository.delete(document);

        // Ghi lại lịch sử xóa tài liệu
//        User currentUser = getCurrentUser();
//        accessHistoryService.recordAccess(currentUser, document, "Deleted Document");
    }

    // Lấy thông tin người dùng hiện tại từ SecurityContext
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }
}
