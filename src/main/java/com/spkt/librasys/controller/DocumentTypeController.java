package com.spkt.librasys.controller;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.request.document.DocumentTypeCreateRequest;
import com.spkt.librasys.dto.response.ApiResponse;
import com.spkt.librasys.dto.response.document.DocumentTypeResponse;
import com.spkt.librasys.service.DocumentTypeService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Lớp Controller quản lý các yêu cầu liên quan đến loại tài liệu.
 * Cung cấp các endpoint cho việc tạo mới, cập nhật, truy xuất và xóa loại tài liệu.
 */
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/document-types")
public class DocumentTypeController {

    DocumentTypeService documentTypeService;

    /**
     * Truy xuất tất cả các loại tài liệu với phân trang.
     * Endpoint này chỉ có thể truy cập bởi người dùng có quyền 'ADMIN' hoặc 'MANAGER'.
     *
     * @param id dung de truy van tim documentType
     * @return ApiResponse chứa danh sách các loại tài liệu theo phân trang.
     */
    @GetMapping("/{id}")
    public ApiResponse<DocumentTypeResponse> getIdDocumentTypes(@PathVariable Long id) {
        DocumentTypeResponse documentTypes = documentTypeService.getIdDocumentType(id);
        return ApiResponse.<DocumentTypeResponse>builder()
                .message("lay id loại tài liệu đã được truy xuất thành công")
                .result(documentTypes)
                .build();
    }

    /**
     * Truy xuất tất cả các loại tài liệu với phân trang.
     * Endpoint này chỉ có thể truy cập bởi người dùng có quyền 'ADMIN' hoặc 'MANAGER'.
     *
     * @param pageable Thông tin phân trang (số trang, kích thước trang, hướng sắp xếp).
     * @return ApiResponse chứa danh sách các loại tài liệu theo phân trang.
     */
    @GetMapping
    public ApiResponse<PageDTO<DocumentTypeResponse>> getAllDocumentTypes(Pageable pageable) {
        Page<DocumentTypeResponse> documentTypes = documentTypeService.getAllDocumentTypes(pageable);
        PageDTO<DocumentTypeResponse> pageDTO = new PageDTO<>(documentTypes);
        return ApiResponse.<PageDTO<DocumentTypeResponse>>builder()
                .message("Các loại tài liệu đã được truy xuất thành công")
                .result(pageDTO)
                .build();
    }

    /**
     * Tạo mới một loại tài liệu.
     * Endpoint này chỉ có thể truy cập bởi người dùng có quyền 'ADMIN'.
     *
     * @param request Yêu cầu tạo loại tài liệu, bao gồm thông tin cần thiết để tạo loại tài liệu mới.
     * @return ApiResponse chứa loại tài liệu mới được tạo thành công.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ApiResponse<DocumentTypeResponse> createDocumentType(@Valid @RequestBody DocumentTypeCreateRequest request) {
        DocumentTypeResponse documentType = documentTypeService.createDocumentType(request);
        return ApiResponse.<DocumentTypeResponse>builder()
                .message("Loại tài liệu đã được tạo thành công")
                .result(documentType)
                .build();
    }

    /**
     * Cập nhật thông tin một loại tài liệu đã có.
     * Endpoint này chỉ có thể truy cập bởi người dùng có quyền 'ADMIN'.
     *
     * @param documentTypeId ID của loại tài liệu cần cập nhật.
     * @param request Yêu cầu cập nhật, bao gồm thông tin mới để thay đổi loại tài liệu.
     * @return ApiResponse chứa loại tài liệu đã được cập nhật.
     */
    @PutMapping("/{documentTypeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<DocumentTypeResponse> updateDocumentType(@PathVariable Long documentTypeId, @RequestBody DocumentTypeCreateRequest request) {
        DocumentTypeResponse documentType = documentTypeService.updateDocumentType(documentTypeId, request);
        return ApiResponse.<DocumentTypeResponse>builder()
                .message("Loại tài liệu đã được cập nhật thành công")
                .result(documentType)
                .build();
    }

    /**
     * Xóa một loại tài liệu khỏi hệ thống.
     * Endpoint này chỉ có thể truy cập bởi người dùng có quyền 'ADMIN'.
     *
     * @param documentTypeId ID của loại tài liệu cần xóa.
     * @return ApiResponse thông báo kết quả xóa loại tài liệu thành công.
     */
    @DeleteMapping("/{documentTypeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteDocumentType(@PathVariable Long documentTypeId) {
        documentTypeService.deleteDocumentType(documentTypeId);
        return ApiResponse.<Void>builder()
                .message("Loại tài liệu đã được xóa thành công")
                .build();
    }
}
