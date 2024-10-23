package com.spkt.librasys.controller;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.request.document.DocumentCreateRequest;
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
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/documents")
public class DocumentController {

    DocumentService documentService;

    @PostMapping
    public ApiResponse<DocumentResponse> createDocument(@RequestBody @Valid DocumentCreateRequest request) {
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
    public ApiResponse<DocumentResponse> updateDocument(@PathVariable Long id, @RequestBody @Valid DocumentUpdateRequest request) {
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
    public ApiResponse<PageDTO<DocumentResponse>> getAllDocuments(Pageable pageable){
        Page<DocumentResponse> responseList = documentService.getAllDocuments(pageable);
        PageDTO<DocumentResponse> pageDTO = new PageDTO<>(responseList);
        return ApiResponse.<PageDTO<DocumentResponse>>builder()
                .message("Documents getAll successfully")
                .result(pageDTO)
                .build();

    }

    @GetMapping("/search")
    public ApiResponse<PageDTO<DocumentResponse>> getAllDocuments(
            @ModelAttribute DocumentSearchRequest searchRequest,
            Pageable pageable) {
        Page<DocumentResponse> responseList = documentService.searchDocuments(
                searchRequest,
                pageable
        );
        PageDTO<DocumentResponse> pageDTO = new PageDTO<>(responseList);
        return ApiResponse.<PageDTO<DocumentResponse>>builder()
                .message("Documents searched successfully")
                .result(pageDTO)
                .build();
    }
    @PostMapping("/{id}/classify")
    public ApiResponse<Void> classifyDocument(@PathVariable Long id, @RequestParam String newTypeName) {
        documentService.classifyDocument(id, newTypeName);
        return ApiResponse.<Void>builder()
                .message("Document classified successfully")
                .build();
    }
    @PostMapping("/{id}/favorite")
    public ApiResponse<Void> favoriteDocument(@PathVariable Long id) {
        documentService.favoriteDocument(id);
        return ApiResponse.<Void>builder()
                .message("Document marked as favorite successfully")
                .build();
    }
    @GetMapping("/favorites")
    public ApiResponse<PageDTO<DocumentResponse>> getFavoriteDocuments(Pageable pageable) {
        Page<DocumentResponse> responseList = documentService.getFavoriteDocuments(pageable);
        PageDTO<DocumentResponse> pageDTO = new PageDTO<>(responseList);
        return ApiResponse.<PageDTO<DocumentResponse>>builder()
                .message("Favorite documents retrieved successfully")
                .result(pageDTO)
                .build();
    }
    //@GetMapping("/{id}/download")
    //public ApiResponse<Void> downloadDocument(@PathVariable Long id, @RequestParam Long userId) {
    //    documentService.downloadDocument(id, userId);
    //    return ApiResponse.<Void>builder()
    //            .message("Document downloaded successfully")
    //            .build();
    //}
    @DeleteMapping("/{id}/favorite")
    public ApiResponse<Void> unFavoriteDocument(@PathVariable Long id) {
        documentService.unFavoriteDocument(id);
        return ApiResponse.<Void>builder()
                .message("Document removed from favorites successfully")
                .build();
    }
    @GetMapping("/{id}/is-favorite")
    public ApiResponse<Boolean> isFavoriteDocument(@PathVariable Long id) {
        boolean isFavorite = documentService.isFavoriteDocument(id);
        return ApiResponse.<Boolean>builder()
                .message("Favorite status retrieved successfully")
                .result(isFavorite)
                .build();
    }
}
