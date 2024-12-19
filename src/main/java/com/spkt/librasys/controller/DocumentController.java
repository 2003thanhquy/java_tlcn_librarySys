package com.spkt.librasys.controller;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.request.document.DocumentCreateRequest;
import com.spkt.librasys.dto.request.document.DocumentQuantityUpdateRequest;
import com.spkt.librasys.dto.request.document.DocumentSearchRequest;
import com.spkt.librasys.dto.request.document.DocumentUpdateRequest;
import com.spkt.librasys.dto.response.ApiResponse;
import com.spkt.librasys.dto.response.document.DocumentResponse;
import com.spkt.librasys.service.DocumentService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;

/**
 * Lớp Controller quản lý các yêu cầu API liên quan đến tài liệu.
 * Cung cấp các endpoint để tạo, cập nhật, tìm kiếm, và quản lý tài liệu.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/documents")
public class DocumentController {

    DocumentService documentService;

    /**
     * Tạo mới một tài liệu.
     * Endpoint này cho phép người dùng tạo tài liệu mới.
     *
     * @param request Yêu cầu tạo tài liệu, bao gồm các thông tin chi tiết của tài liệu.
     * @return ApiResponse chứa thông tin tài liệu đã được tạo.
     */
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ApiResponse<DocumentResponse> createDocument(
            @ModelAttribute @Valid DocumentCreateRequest request) {
        DocumentResponse response = documentService.createDocument(request);
        return ApiResponse.<DocumentResponse>builder()
                .message("Tài liệu đã được tạo thành công")
                .result(response)
                .build();
    }

    /**
     * Lấy thông tin tài liệu theo ID.
     * Endpoint này trả về chi tiết của tài liệu dựa trên ID.
     *
     * @param id ID của tài liệu cần lấy thông tin.
     * @return ApiResponse chứa thông tin tài liệu.
     */
    @GetMapping("/{id}")
    public ApiResponse<DocumentResponse> getDocumentById(@PathVariable Long id) {
        DocumentResponse response = documentService.getDocumentById(id);
        return ApiResponse.<DocumentResponse>builder()
                .message("Tài liệu đã được lấy thành công")
                .result(response)
                .build();
    }

    /**
     * Cập nhật thông tin tài liệu theo ID.
     * Endpoint này cho phép cập nhật các thông tin của tài liệu.
     *
     * @param id ID của tài liệu cần cập nhật.
     * @param request Yêu cầu cập nhật tài liệu bao gồm các thông tin mới.
     * @return ApiResponse chứa thông tin tài liệu đã được cập nhật.
     */
    @PutMapping(value = "/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ApiResponse<DocumentResponse> updateDocument(
            @PathVariable Long id,
            @ModelAttribute @Valid DocumentUpdateRequest request) {
        DocumentResponse response = documentService.updateDocument(id, request);
        return ApiResponse.<DocumentResponse>builder()
                .message("Tài liệu đã được cập nhật thành công")
                .result(response)
                .build();
    }

    /**
     * Xóa tài liệu theo ID.
     * Endpoint này cho phép xóa một tài liệu dựa trên ID.
     *
     * @param id ID của tài liệu cần xóa.
     * @return ApiResponse thông báo kết quả của thao tác xóa.
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ApiResponse.<Void>builder()
                .message("Tài liệu đã được xóa thành công")
                .build();
    }

    /**
     * Lấy tất cả tài liệu với phân trang và sắp xếp.
     * Endpoint này cho phép lấy tất cả tài liệu với các tùy chọn phân trang và sắp xếp.
     *
     * @param page Số trang cho phân trang.
     * @param size Kích thước của mỗi trang.
     * @param sort Trường để sắp xếp tài liệu.
     * @param direction Hướng sắp xếp ("asc" hoặc "desc").
     * @return ApiResponse chứa danh sách tài liệu với phân trang.
     */
    @GetMapping
    public ApiResponse<PageDTO<DocumentResponse>> getAllDocuments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "documentName") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort sortBy;
        try {
            Sort.Direction sortDirection = Sort.Direction.fromString(direction);
            sortBy = Sort.by(sortDirection, sort);
        } catch (IllegalArgumentException e) {
            return ApiResponse.<PageDTO<DocumentResponse>>builder()
                    .message("Hướng sắp xếp không hợp lệ. Sử dụng 'asc' hoặc 'desc'.")
                    .result(null)
                    .build();
        }
        Pageable pageable = PageRequest.of(page, size, sortBy);
        Page<DocumentResponse> responseList = documentService.getAllDocuments(pageable);
        PageDTO<DocumentResponse> pageDTO = new PageDTO<>(responseList);
        return ApiResponse.<PageDTO<DocumentResponse>>builder()
                .message("Tài liệu đã được lấy thành công")
                .result(pageDTO)
                .build();
    }

    /**
     * Tìm kiếm tài liệu dựa trên tiêu chí và phân trang.
     * Endpoint này cho phép tìm kiếm tài liệu theo các tiêu chí tìm kiếm với phân trang.
     *
     * @param searchRequest Tiêu chí tìm kiếm tài liệu.
     * @param pageable Thông tin phân trang.
     * @return ApiResponse chứa danh sách tài liệu phù hợp với tiêu chí tìm kiếm.
     */
    @GetMapping("/search")
    public ApiResponse<PageDTO<DocumentResponse>> getAllDocuments(
            @ModelAttribute DocumentSearchRequest searchRequest,
            Pageable pageable) {
        Page<DocumentResponse> responseList = documentService.searchDocuments(searchRequest, pageable);
        PageDTO<DocumentResponse> pageDTO = new PageDTO<>(responseList);
        return ApiResponse.<PageDTO<DocumentResponse>>builder()
                .message("Tài liệu đã được tìm kiếm thành công")
                .result(pageDTO)
                .build();
    }

    /**
     * Phân loại tài liệu theo tên loại mới.
     * Endpoint này cho phép phân loại tài liệu theo tên loại mới.
     *
     * @param id ID của tài liệu cần phân loại.
     * @param newTypeName Tên loại mới cho tài liệu.
     * @return ApiResponse thông báo kết quả phân loại.
     */
    @PostMapping("/{id}/classify")
    public ApiResponse<Void> classifyDocument(@PathVariable Long id, @RequestParam String newTypeName) {
        documentService.classifyDocument(id, newTypeName);
        return ApiResponse.<Void>builder()
                .message("Tài liệu đã được phân loại thành công")
                .build();
    }

    /**
     * Đánh dấu tài liệu là yêu thích.
     * Endpoint này cho phép đánh dấu tài liệu là yêu thích.
     *
     * @param id ID của tài liệu cần đánh dấu yêu thích.
     * @return ApiResponse thông báo kết quả của thao tác.
     */
    @PostMapping("/{id}/favorite")
    public ApiResponse<Void> favoriteDocument(@PathVariable Long id) {
        documentService.favoriteDocument(id);
        return ApiResponse.<Void>builder()
                .message("Tài liệu đã được đánh dấu yêu thích thành công")
                .build();
    }

    /**
     * Lấy tất cả tài liệu yêu thích với phân trang.
     * Endpoint này cho phép lấy tất cả tài liệu yêu thích với phân trang.
     *
     * @param pageable Thông tin phân trang.
     * @return ApiResponse chứa danh sách tài liệu yêu thích.
     */
    @GetMapping("/favorites")
    public ApiResponse<PageDTO<DocumentResponse>> getFavoriteDocuments(Pageable pageable) {
        Page<DocumentResponse> responseList = documentService.getFavoriteDocuments(pageable);
        PageDTO<DocumentResponse> pageDTO = new PageDTO<>(responseList);
        return ApiResponse.<PageDTO<DocumentResponse>>builder()
                .message("Tài liệu yêu thích đã được lấy thành công")
                .result(pageDTO)
                .build();
    }

    /**
     * Gỡ tài liệu khỏi danh sách yêu thích.
     * Endpoint này cho phép gỡ tài liệu khỏi danh sách yêu thích.
     *
     * @param id ID của tài liệu cần gỡ khỏi yêu thích.
     * @return ApiResponse thông báo kết quả của thao tác.
     */
    @DeleteMapping("/{id}/favorite")
    public ApiResponse<Void> unFavoriteDocument(@PathVariable Long id) {
        documentService.unFavoriteDocument(id);
        return ApiResponse.<Void>builder()
                .message("Tài liệu đã được gỡ khỏi danh sách yêu thích thành công")
                .build();
    }

    /**
     * Kiểm tra xem tài liệu có được đánh dấu là yêu thích hay không.
     * Endpoint này trả về trạng thái yêu thích của tài liệu.
     *
     * @param id ID của tài liệu cần kiểm tra.
     * @return ApiResponse chứa trạng thái yêu thích của tài liệu.
     */
    @GetMapping("/{id}/is-favorite")
    public ApiResponse<Boolean> isFavoriteDocument(@PathVariable Long id) {
        boolean isFavorite = documentService.isFavoriteDocument(id);
        return ApiResponse.<Boolean>builder()
                .message("Trạng thái yêu thích đã được lấy thành công")
                .result(isFavorite)
                .build();
    }

    /**
     * Xóa nhiều tài liệu theo danh sách ID.
     * Endpoint này cho phép xóa nhiều tài liệu cùng lúc bằng cách truyền vào danh sách ID của các tài liệu.
     *
     * @param documentIds Danh sách ID của các tài liệu cần xóa.
     * @return ApiResponse thông báo kết quả xóa tài liệu.
     */
    @DeleteMapping("/batch")
    public ApiResponse<Void> deleteDocumentsByIds(@RequestBody List<Long> documentIds) {
        documentService.deleteDocumentsByIds(documentIds);
        return ApiResponse.<Void>builder()
                .message("Tài liệu đã được đánh dấu là không khả dụng thành công")
                .build();
    }

    /**
     * Cập nhật số lượng tài liệu trong kho hoặc trên giá.
     * Endpoint này cho phép cập nhật số lượng tài liệu trong kho hoặc trên giá.
     *
     * @param request Yêu cầu cập nhật số lượng tài liệu, bao gồm ID tài liệu và số lượng mới.
     * @return ApiResponse thông báo kết quả cập nhật.
     */
    @PostMapping("/update-quantity")
    public ApiResponse<Void> updateDocumentQuantity(
            @RequestBody @Valid DocumentQuantityUpdateRequest request) {
        documentService.updateQuantity(request);
        return ApiResponse.<Void>builder()
                .message("Số lượng tài liệu đã được cập nhật thành công")
                .build();
    }

    /**
     * Đọc nội dung của một trang tài liệu dưới dạng mã hóa base64.
     * Endpoint này trả về nội dung của một trang tài liệu dưới dạng mã hóa base64.
     *
     * @param documentId ID của tài liệu cần đọc.
     * @param page Số trang cần đọc.
     * @return ApiResponse chứa nội dung trang tài liệu ở định dạng base64.
     */
    @GetMapping("/{documentId}/read")
    public ApiResponse<String> readDocument(
            @PathVariable Long documentId,
            @RequestParam(value = "page", defaultValue = "1") int page) {
        try {
            byte[] pageContent = documentService.getDocumentPageContent(documentId, page);
            String base64Content = Base64.getEncoder().encodeToString(pageContent);
            return ApiResponse.<String>builder()
                    .message("Nội dung trang tài liệu đã được lấy thành công")
                    .result(base64Content)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<String>builder()
                    .code(1001)
                    .message("Lỗi khi lấy nội dung trang tài liệu: " + e.getMessage())
                    .build();
        }
    }
}

