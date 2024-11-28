package com.spkt.librasys.controller;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.response.ApiResponse;
import com.spkt.librasys.dto.response.programclass.ProgramClassResponse;
import com.spkt.librasys.service.ProgramClassService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Lớp Controller xử lý các yêu cầu liên quan đến Chương trình học (ProgramClass).
 * Cung cấp các API để quản lý Chương trình học: tạo mới, cập nhật, xóa, tải lên từ file Excel và truy vấn dữ liệu.
 */
@RestController
@RequestMapping("/api/v1/program-classes")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProgramClassController {

    ProgramClassService programClassService;

    /**
     * API tải lên danh sách Chương trình học từ file Excel.
     * Endpoint này cho phép người dùng upload file Excel chứa thông tin các Chương trình học.
     *
     * @param file File Excel chứa thông tin Chương trình học.
     * @return ApiResponse thông báo kết quả tải lên.
     */
    @PostMapping("/upload")
    @RateLimiter(name = "programClassUploadLimiter")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<Void> uploadProgramClasses(@RequestParam("file") MultipartFile file) {
        log.info("Bắt đầu upload chương trình học từ file Excel");

        if (file.isEmpty()) {
            return ApiResponse.<Void>builder()
                    .code(400)
                    .message("File không được để trống")
                    .build();
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            return ApiResponse.<Void>builder()
                    .code(400)
                    .message("Chỉ hỗ trợ file Excel (.xlsx, .xls)")
                    .build();
        }

        programClassService.saveProgramClassesFromExcel(file);
        return ApiResponse.<Void>builder()
                .message("Dữ liệu ProgramClass đã được chèn thành công")
                .build();
    }

    /**
     * API lấy thông tin Chương trình học theo ID.
     * Endpoint này cho phép người dùng lấy thông tin chi tiết của một Chương trình học dựa trên ID.
     *
     * @param id ID của Chương trình học cần lấy.
     * @return ApiResponse chứa thông tin Chương trình học.
     */
    @GetMapping("/{id}")
    public ApiResponse<ProgramClassResponse> getProgramClassById(@PathVariable Long id) {
        ProgramClassResponse response = programClassService.getProgramClassById(id);
        return ApiResponse.<ProgramClassResponse>builder()
                .message("ProgramClass retrieved successfully")
                .result(response)
                .build();
    }

    /**
     * API lấy danh sách các Chương trình học với phân trang.
     * Endpoint này cho phép người dùng truy vấn danh sách các Chương trình học với phân trang.
     *
     * @param pageable Thông tin phân trang (số trang, kích thước trang).
     * @return ApiResponse chứa danh sách Chương trình học theo phân trang.
     */
    @GetMapping
    public ApiResponse<PageDTO<ProgramClassResponse>> getAllProgramClasses(Pageable pageable) {
        Page<ProgramClassResponse> responsePage = programClassService.getAllProgramClasses(pageable);
        PageDTO<ProgramClassResponse> pageDTO = new PageDTO<>(responsePage);
        return ApiResponse.<PageDTO<ProgramClassResponse>>builder()
                .message("ProgramClasses retrieved successfully")
                .result(pageDTO)
                .build();
    }

    /**
     * API tạo mới một Chương trình học.
     * Endpoint này cho phép người dùng tạo mới một Chương trình học.
     *
     * @param request Thông tin của Chương trình học cần tạo mới.
     * @return ApiResponse chứa thông tin Chương trình học vừa được tạo.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<ProgramClassResponse> createProgramClass(
            @Valid @RequestBody ProgramClassResponse request) {
        ProgramClassResponse response = programClassService.createProgramClass(request);
        return ApiResponse.<ProgramClassResponse>builder()
                .message("ProgramClass created successfully")
                .result(response)
                .build();
    }

    /**
     * API cập nhật thông tin Chương trình học.
     * Endpoint này cho phép người dùng cập nhật thông tin của một Chương trình học theo ID.
     *
     * @param id ID của Chương trình học cần cập nhật.
     * @param request Thông tin cập nhật cho Chương trình học.
     * @return ApiResponse chứa thông tin Chương trình học đã được cập nhật.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<ProgramClassResponse> updateProgramClass(
            @PathVariable Long id,
            @Valid @RequestBody ProgramClassResponse request) {
        ProgramClassResponse response = programClassService.updateProgramClass(id, request);
        return ApiResponse.<ProgramClassResponse>builder()
                .message("ProgramClass updated successfully")
                .result(response)
                .build();
    }

    /**
     * API xóa một Chương trình học theo ID.
     * Endpoint này cho phép người dùng xóa một Chương trình học theo ID.
     *
     * @param id ID của Chương trình học cần xóa.
     * @return ApiResponse thông báo xóa thành công.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<Void> deleteProgramClass(@PathVariable Long id) {
        programClassService.deleteProgramClass(id);
        return ApiResponse.<Void>builder()
                .message("ProgramClass deleted successfully")
                .build();
    }

    /**
     * API xóa nhiều Chương trình học.
     * Endpoint này cho phép người dùng xóa nhiều Chương trình học dựa trên danh sách ID.
     *
     * @param ids Danh sách các ID của Chương trình học cần xóa.
     * @return ApiResponse thông báo xóa thành công.
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<Void> deleteProgramClasses(@RequestBody List<Long> ids) {
        programClassService.deleteProgramClasses(ids);
        return ApiResponse.<Void>builder()
                .message("Selected ProgramClasses deleted successfully")
                .build();
    }
}
