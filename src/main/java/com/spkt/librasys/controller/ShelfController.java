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

/**
 * Controller quản lý các yêu cầu liên quan đến kệ trong hệ thống.
 * Các API này cho phép tạo mới, cập nhật, lấy thông tin và xóa các kệ.
 */
@RestController
@RequestMapping("/api/v1/shelves")
@RequiredArgsConstructor
public class ShelfController {

    private final ShelfService shelfService;

    /**
     * Tạo mới một kệ trong hệ thống.
     *
     * @param request yêu cầu tạo mới kệ, chứa thông tin kệ
     * @return ApiResponse chứa thông tin kệ mới được tạo
     */
    @PostMapping
    public ApiResponse<ShelfResponse> createShelf(@Valid @RequestBody ShelfRequest request) {
        ShelfResponse response = shelfService.createShelf(request);
        return ApiResponse.<ShelfResponse>builder()
                .message("Kệ tạo thành công")
                .result(response)
                .build();
    }

    /**
     * Cập nhật thông tin của một kệ theo ID.
     *
     * @param id ID của kệ cần cập nhật
     * @param request yêu cầu cập nhật kệ, chứa thông tin kệ mới
     * @return ApiResponse chứa thông tin kệ đã được cập nhật
     */
    @PutMapping("/{id}")
    public ApiResponse<ShelfResponse> updateShelf(
            @PathVariable Long id,
            @Valid @RequestBody ShelfRequest request) {
        ShelfResponse response = shelfService.updateShelf(id, request);
        return ApiResponse.<ShelfResponse>builder()
                .message("Kệ cập nhật thành công")
                .result(response)
                .build();
    }

    /**
     * Lấy thông tin kệ theo ID.
     *
     * @param id ID của kệ cần lấy thông tin
     * @return ApiResponse chứa thông tin của kệ
     */
    @GetMapping("/{id}")
    public ApiResponse<ShelfResponse> getShelfById(@PathVariable Long id) {
        ShelfResponse response = shelfService.getShelfById(id);
        return ApiResponse.<ShelfResponse>builder()
                .message("Lấy kệ thành công")
                .result(response)
                .build();
    }

    /**
     * Lấy danh sách tất cả các kệ trong hệ thống với phân trang.
     *
     * @param pageable thông tin phân trang
     * @return ApiResponse chứa danh sách các kệ với thông tin phân trang
     */
    @GetMapping
    public ApiResponse<PageDTO<ShelfResponse>> getAllShelves(Pageable pageable) {
        PageDTO<ShelfResponse> response = shelfService.getAllShelves(pageable);
        return ApiResponse.<PageDTO<ShelfResponse>>builder()
                .message("Lấy danh sách kệ thành công")
                .result(response)
                .build();
    }

    /**
     * Xóa một kệ theo ID.
     *
     * @param id ID của kệ cần xóa
     * @return ApiResponse thông báo xóa kệ thành công
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteShelf(@PathVariable Long id) {
        shelfService.deleteShelf(id);
        return ApiResponse.<Void>builder()
                .message("Kệ xóa thành công")
                .build();
    }
}
