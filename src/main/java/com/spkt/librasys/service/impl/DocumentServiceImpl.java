package com.spkt.librasys.service.impl;

import com.spkt.librasys.constant.PredefinedRole;
import com.spkt.librasys.dto.request.document.DocumentCreateRequest;
import com.spkt.librasys.dto.request.document.DocumentSearchRequest;
import com.spkt.librasys.dto.request.document.DocumentUpdateRequest;
import com.spkt.librasys.dto.response.document.DocumentResponse;
import com.spkt.librasys.entity.Document;
import com.spkt.librasys.entity.DocumentType;
import com.spkt.librasys.entity.User;
import com.spkt.librasys.entity.enums.DocumentStatus;
import com.spkt.librasys.exception.AppException;
import com.spkt.librasys.exception.ErrorCode;
import com.spkt.librasys.mapper.DocumentMapper;
import com.spkt.librasys.repository.document.DocumentRepository;
import com.spkt.librasys.repository.document.DocumentTypeRepository;
import com.spkt.librasys.repository.access.UserRepository;
import com.spkt.librasys.service.AuthenticationService;
import com.spkt.librasys.service.DocumentService;
import com.spkt.librasys.repository.specification.DocumentSpecification;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

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
    AuthenticationService authenticationService;

    @Override
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public DocumentResponse createDocument(DocumentCreateRequest request) {
        DocumentType documentType = documentTypeRepository.findById(request.getDocumentTypeId())
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));

        Document document = documentMapper.toDocument(request);
        document.setDocumentType(documentType);
        document.setStatus(DocumentStatus.AVAILABLE);  // Thiết lập trạng thái mặc định là "AVAILABLE"
        document.setAvailableCount(request.getAvailableCount()); // Thiết lập số lượng sách có sẵn bằng tổng số lượng nhập vào

        Document savedDocument = documentRepository.save(document);
        return documentMapper.toDocumentResponse(savedDocument);
    }

    @Override
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public DocumentResponse updateDocument(Long id, DocumentUpdateRequest request) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));

        if (request.getDocumentTypeId() != null) {
            DocumentType documentType = documentTypeRepository.findById(request.getDocumentTypeId())
                    .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_TYPE_NOT_FOUND));
            document.setDocumentType(documentType);
        }


        documentMapper.updateDocument(document, request);

        Document updatedDocument = documentRepository.save(document);
        return documentMapper.toDocumentResponse(updatedDocument);
    }

    @Override
    public DocumentResponse getDocumentById(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));
        return documentMapper.toDocumentResponse(document);
    }

    @Override
    public Page<DocumentResponse> searchDocuments(DocumentSearchRequest request, Pageable pageable) {
        Specification<Document> spec = Specification.where(null); // Khởi tạo với null để thêm các điều kiện sau

        // Kiểm tra và thêm điều kiện tìm kiếm dựa trên giá trị của request
        if (request.getDocumentName() != null ) {
            spec = spec.and(DocumentSpecification.hasTitle(request.getDocumentName().trim()));
        }
        if (request.getAuthor() != null ) {
            spec = spec.and(DocumentSpecification.hasAuthor(request.getAuthor().trim()));
        }
        if (request.getPublisher() != null) {
            spec = spec.and(DocumentSpecification.hasPublisher(request.getPublisher().trim()));
        }
        if (request.getDocumentTypeId() != null) {
            spec = spec.and(DocumentSpecification.hasDocumentTypeId(request.getDocumentTypeId()));
        }
        spec = spec.and((root, query, builder) ->
                builder.equal(root.get("status"), DocumentStatus.AVAILABLE));


        // Thực hiện tìm kiếm với Specification đã được thiết lập
        Page<Document> documents = documentRepository.findAll(spec, pageable);
        return documents.map(documentMapper::toDocumentResponse);
    }


    @Override
    public Page<DocumentResponse> getAllDocuments(Pageable pageable) {
        Specification<Document> spec = Specification.where(null);
        User userCurrent = authenticationService.getCurrentUser();
        if (userCurrent == null || userHasRole(userCurrent, PredefinedRole.USER_ROLE)) {
            spec = spec.and((root, query, builder) -> builder.equal(root.get("status"), DocumentStatus.AVAILABLE));
        }

        Page<Document> documents = documentRepository.findAll(spec, pageable);
        return documents.map(documentMapper::toDocumentResponse);
    }
    private boolean userHasRole(User user, String roleName) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName().equals(roleName));
    }

    @Override
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public void deleteDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));
        document.setStatus(DocumentStatus.UNAVAILABLE);
        documentRepository.save(document);
        //documentRepository.delete(document);

        // Ghi lại lịch sử xóa tài liệu
//        User currentUser = getCurrentUser();
//        accessHistoryService.recordAccess(currentUser, document, "Deleted Document");
    }

}
