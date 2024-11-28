package com.spkt.librasys.service;

import com.spkt.librasys.dto.request.document.DocumentTypeCreateRequest;
import com.spkt.librasys.dto.response.document.DocumentTypeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interface cung cấp các phương thức để quản lý các loại tài liệu trong hệ thống thư viện.
 */
public interface DocumentTypeService {

    /**
     * Lấy danh sách tất cả các loại tài liệu với phân trang.
     *
     * @param pageable Thông tin phân trang.
     * @return Trang chứa các loại tài liệu.
     */
    Page<DocumentTypeResponse> getAllDocumentTypes(Pageable pageable);

    /**
     * Tạo mới một loại tài liệu.
     *
     * @param request DTO chứa thông tin của loại tài liệu cần tạo.
     * @return DTO chứa thông tin của loại tài liệu đã tạo.
     */
    DocumentTypeResponse createDocumentType(DocumentTypeCreateRequest request);

    /**
     * Cập nhật thông tin của một loại tài liệu.
     *
     * @param id      ID của loại tài liệu cần cập nhật.
     * @param request DTO chứa thông tin mới để cập nhật loại tài liệu.
     * @return DTO chứa thông tin của loại tài liệu sau khi cập nhật.
     */
    DocumentTypeResponse updateDocumentType(Long id, DocumentTypeCreateRequest request);

    /**
     * Xóa một loại tài liệu theo ID.
     *
     * @param id ID của loại tài liệu cần xóa.
     */
    void deleteDocumentType(Long id);
}
