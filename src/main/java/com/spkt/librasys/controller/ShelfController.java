package com.spkt.librasys.controller;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.request.shelf.ShelfRequest;
import com.spkt.librasys.dto.response.ApiResponse;
import com.spkt.librasys.dto.response.shelf.ShelfResponse;
import com.spkt.librasys.service.ShelfService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shelves")
@RequiredArgsConstructor
public class ShelfController {

    private final ShelfService shelfService;

    @PostMapping
    public ApiResponse<ShelfResponse> createShelf(@Valid @RequestBody ShelfRequest request) {
        ShelfResponse response = shelfService.createShelf(request);
        return ApiResponse.<ShelfResponse>builder()
                .message("Shelf created successfully")
                .result(response)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<ShelfResponse> updateShelf(
            @PathVariable Long id,
            @Valid @RequestBody ShelfRequest request) {
        ShelfResponse response = shelfService.updateShelf(id, request);
        return ApiResponse.<ShelfResponse>builder()
                .message("Shelf updated successfully")
                .result(response)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ShelfResponse> getShelfById(@PathVariable Long id) {
        ShelfResponse response = shelfService.getShelfById(id);
        return ApiResponse.<ShelfResponse>builder()
                .message("Shelf retrieved successfully")
                .result(response)
                .build();
    }

    @GetMapping
    public ApiResponse<PageDTO<ShelfResponse>> getAllShelves(Pageable pageable) {
        PageDTO<ShelfResponse> response = shelfService.getAllShelves(pageable);
        return ApiResponse.<PageDTO<ShelfResponse>>builder()
                .message("Shelves retrieved successfully")
                .result(response)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteShelf(@PathVariable Long id) {
        shelfService.deleteShelf(id);
        return ApiResponse.<Void>builder()
                .message("Shelf deleted successfully")
                .build();
    }
}
