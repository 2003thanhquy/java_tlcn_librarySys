package com.spkt.librasys.controller;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.request.document.DocumentTypeCreateRequest;
import com.spkt.librasys.dto.response.ApiResponse;
import com.spkt.librasys.dto.response.document.DocumentTypeResponse;
import com.spkt.librasys.service.DocumentTypeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/document-types")
public class DocumentTypeController {

    DocumentTypeService documentTypeService;

    // Lấy tất cả các loại tài liệu
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ApiResponse<PageDTO<DocumentTypeResponse>> getAllDocumentTypes(Pageable pageable) {
        Page<DocumentTypeResponse> documentTypes = documentTypeService.getAllDocumentTypes(pageable);
        PageDTO<DocumentTypeResponse> pageDTO = new PageDTO<>(documentTypes);
        return ApiResponse.<PageDTO<DocumentTypeResponse>>builder()
                .message("Document types retrieved successfully")
                .result(pageDTO)
                .build();
    }

    // Tạo một loại tài liệu mới
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<DocumentTypeResponse> createDocumentType(@RequestBody DocumentTypeCreateRequest request) {
        DocumentTypeResponse documentType = documentTypeService.createDocumentType(request);
        return ApiResponse.<DocumentTypeResponse>builder()
                .message("Document type created successfully")
                .result(documentType)
                .build();
    }

    // Cập nhật thông tin loại tài liệu
    @PutMapping("/{documentTypeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<DocumentTypeResponse> updateDocumentType(@PathVariable String documentTypeId, @RequestBody DocumentTypeCreateRequest request) {
        DocumentTypeResponse documentType = documentTypeService.updateDocumentType(documentTypeId, request);
        return ApiResponse.<DocumentTypeResponse>builder()
                .message("Document type updated successfully")
                .result(documentType)
                .build();
    }

    // Xóa một loại tài liệu khỏi hệ thống
    @DeleteMapping("/{documentTypeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteDocumentType(@PathVariable String documentTypeId) {
        documentTypeService.deleteDocumentType(documentTypeId);
        return ApiResponse.<Void>builder()
                .message("Document type deleted successfully")
                .build();
    }
}