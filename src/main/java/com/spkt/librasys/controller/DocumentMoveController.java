package com.spkt.librasys.controller;

import com.spkt.librasys.dto.request.document.DocumentMoveRequest;
import com.spkt.librasys.dto.response.ApiResponse;
import com.spkt.librasys.service.DocumentMoveService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

/**
 * Lớp Controller quản lý các thao tác di chuyển tài liệu.
 * Controller này bao gồm các phương thức để di chuyển tài liệu giữa các kệ và kho.
 */
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/documents")
public class DocumentMoveController {

    DocumentMoveService documentMoveService;

    /**
     * Di chuyển tài liệu đến một kệ chỉ định.
     * Endpoint này cho phép di chuyển tài liệu từ kho hoặc vị trí hiện tại đến một kệ mới.
     *
     * @param request Yêu cầu di chuyển tài liệu, bao gồm thông tin về tài liệu và kệ đích.
     * @return ApiResponse thông báo kết quả của thao tác di chuyển.
     */
    @PostMapping("/move-to-rack")
    public ApiResponse<Void> moveDocument(@RequestBody @Valid DocumentMoveRequest request) {
        documentMoveService.moveDocumentRack(request);
        return ApiResponse.<Void>builder()
                .message("Tài liệu đã được di chuyển thành công đến kệ")
                .build();
    }

    /**
     * Di chuyển tài liệu đến kho.
     * Endpoint này cho phép di chuyển tài liệu từ kệ hoặc vị trí hiện tại đến kho.
     *
     * @param request Yêu cầu di chuyển tài liệu, bao gồm thông tin về tài liệu và kho đích.
     * @return ApiResponse thông báo kết quả của thao tác di chuyển.
     */
    @PostMapping("/move-to-warehouse")
    public ApiResponse<Void> moveDocumentToWarehouse(@RequestBody @Valid DocumentMoveRequest request) {
        documentMoveService.moveDocumentToWarehouse(request);
        return ApiResponse.<Void>builder()
                .message("Tài liệu đã được di chuyển thành công đến kho")
                .build();
    }
}
