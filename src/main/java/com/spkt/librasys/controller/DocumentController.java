package com.spkt.librasys.controller;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.request.documentRequest.DocumentCreateRequest;
import com.spkt.librasys.dto.request.documentRequest.DocumentUpdateRequest;
import com.spkt.librasys.dto.response.ApiResponse;
import com.spkt.librasys.dto.response.documentResponse.DocumentResponse;
import com.spkt.librasys.service.DocumentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/documents")
public class DocumentController {

    DocumentService documentService;

    @PostMapping
    public ApiResponse<DocumentResponse> createDocument(@RequestBody DocumentCreateRequest request) {
        DocumentResponse response = documentService.createDocument(request);
        return ApiResponse.<DocumentResponse>builder()
                .message("Document created successfully")
                .result(response)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<DocumentResponse> getDocumentById(@PathVariable Long id) {
        DocumentResponse response = documentService.getDocumentById(id);
        return ApiResponse.<DocumentResponse>builder()
                .message("Document retrieved successfully")
                .result(response)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<DocumentResponse> updateDocument(@PathVariable Long id, @RequestBody DocumentUpdateRequest request) {
        DocumentResponse response = documentService.updateDocument(id, request);
        return ApiResponse.<DocumentResponse>builder()
                .message("Document updated successfully")
                .result(response)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ApiResponse.<Void>builder()
                .message("Document deleted successfully")
                .build();
    }

    @GetMapping
    public ApiResponse<PageDTO<DocumentResponse>> getAllDocuments(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String publisher,
            @RequestParam(required = false) Long documentTypeId,
            Pageable pageable) {
        Page<DocumentResponse> responseList = documentService.getAllDocuments(title, author, publisher, documentTypeId, pageable);
        PageDTO<DocumentResponse> pageDTO = new PageDTO<>(responseList);
        return ApiResponse.<PageDTO<DocumentResponse>>builder()
                .message("Documents retrieved successfully")
                .result(pageDTO)
                .build();
    }
}
