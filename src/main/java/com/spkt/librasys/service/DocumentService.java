package com.spkt.librasys.service;

import com.spkt.librasys.dto.request.document.DocumentCreateRequest;
import com.spkt.librasys.dto.request.document.DocumentQuantityUpdateRequest;
import com.spkt.librasys.dto.request.document.DocumentSearchRequest;
import com.spkt.librasys.dto.request.document.DocumentUpdateRequest;
import com.spkt.librasys.dto.response.document.DocumentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interface cung cấp các phương thức để quản lý tài liệu trong hệ thống thư viện.
 */
public interface DocumentService {

    /**
     * Tạo mới một tài liệu.
     *
     * @param request DTO chứa thông tin của tài liệu cần tạo.
     * @return DTO chứa thông tin của tài liệu đã tạo.
     */
    DocumentResponse createDocument(DocumentCreateRequest request);

    /**
     * Cập nhật thông tin của một tài liệu theo ID.
     *
     * @param id      ID của tài liệu cần cập nhật.
     * @param request DTO chứa thông tin mới để cập nhật tài liệu.
     * @return DTO chứa thông tin của tài liệu sau khi cập nhật.
     */
    DocumentResponse updateDocument(Long id, DocumentUpdateRequest request);

    /**
     * Lấy thông tin chi tiết của một tài liệu theo ID.
     *
     * @param id ID của tài liệu cần lấy thông tin.
     * @return DTO chứa thông tin chi tiết của tài liệu.
     */
    DocumentResponse getDocumentById(Long id);

    /**
     * Tìm kiếm tài liệu theo các tiêu chí đã cung cấp.
     *
     * @param searchRequest DTO chứa các tham số tìm kiếm (ví dụ: tên tài liệu, tác giả, loại tài liệu).
     * @param pageable      Thông tin phân trang.
     * @return Trang chứa danh sách tài liệu thỏa mãn điều kiện tìm kiếm.
     */
    Page<DocumentResponse> searchDocuments(DocumentSearchRequest searchRequest, Pageable pageable);

    /**
     * Lấy tất cả các tài liệu trong hệ thống với phân trang.
     *
     * @param pageable Thông tin phân trang.
     * @return Trang chứa tất cả tài liệu.
     */
    Page<DocumentResponse> getAllDocuments(Pageable pageable);

    /**
     * Xóa một tài liệu theo ID.
     *
     * @param id ID của tài liệu cần xóa.
     */
    void deleteDocument(Long id);

    /**
     * Lấy danh sách các tài liệu yêu thích với phân trang.
     *
     * @param pageable Thông tin phân trang.
     * @return Trang chứa các tài liệu yêu thích.
     */
    Page<DocumentResponse> getFavoriteDocuments(Pageable pageable);

    /**
     * Phân loại một tài liệu vào một loại mới.
     *
     * @param id           ID của tài liệu cần phân loại.
     * @param newTypeName  Tên loại mới của tài liệu.
     */
    void classifyDocument(Long id, String newTypeName);

    /**
     * Đánh dấu một tài liệu là yêu thích.
     *
     * @param id ID của tài liệu cần đánh dấu yêu thích.
     */
    void favoriteDocument(Long id);

    /**
     * Bỏ đánh dấu yêu thích một tài liệu.
     *
     * @param documentId ID của tài liệu cần bỏ yêu thích.
     */
    void unFavoriteDocument(Long documentId);

    /**
     * Kiểm tra xem tài liệu có phải là tài liệu yêu thích không.
     *
     * @param documentId ID của tài liệu cần kiểm tra.
     * @return true nếu tài liệu là yêu thích, false nếu không.
     */
    boolean isFavoriteDocument(Long documentId);

    /**
     * Xóa một danh sách tài liệu theo danh sách ID.
     *
     * @param documentIds Danh sách các ID tài liệu cần xóa.
     */
    void deleteDocumentsByIds(List<Long> documentIds);

    /**
     * Cập nhật số lượng của một tài liệu.
     *
     * @param request DTO chứa thông tin về tài liệu và số lượng cần cập nhật.
     */
    void updateQuantity(DocumentQuantityUpdateRequest request);

    /**
     * Lấy nội dung của một trang tài liệu dưới dạng byte[].
     *
     * @param documentId ID của tài liệu.
     * @param pageNumber Số trang cần lấy nội dung.
     * @return Mảng byte chứa nội dung trang tài liệu.
     */
    byte[] getDocumentPageContent(Long documentId, int pageNumber);
}
