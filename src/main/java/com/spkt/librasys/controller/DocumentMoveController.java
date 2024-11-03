package com.spkt.librasys.controller;

import com.spkt.librasys.dto.request.document.DocumentMoveRequest;
import com.spkt.librasys.dto.response.ApiResponse;
import com.spkt.librasys.service.DocumentMoveService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/documents")
public class DocumentMoveController {

    DocumentMoveService documentMoveService;

    @PostMapping("/move-to-rack")
    public ApiResponse<Void> moveDocument(@RequestBody @Valid DocumentMoveRequest request) {
        documentMoveService.moveDocumentRack(request);
        return ApiResponse.<Void>builder()
                .message("Document moved successfully")
                .build();
    }
    @PostMapping("/move-to-warehouse")
    public ApiResponse<Void> moveDocumentToWarehouse(@RequestBody @Valid DocumentMoveRequest request) {
        documentMoveService.moveDocumentToWarehouse(request);
        return ApiResponse.<Void>builder()
                .message("Document moved to warehouse successfully")
                .build();
    }
}