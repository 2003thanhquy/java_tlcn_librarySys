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

@RestController
@RequestMapping("/api/v1/program-classes")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProgramClassController {

    ProgramClassService programClassService;

    // API: Upload ProgramClass từ file Excel
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

    // API: Lấy ProgramClass theo ID
    @GetMapping("/{id}")
    public ApiResponse<ProgramClassResponse> getProgramClassById(@PathVariable Long id) {
        ProgramClassResponse response = programClassService.getProgramClassById(id);
        return ApiResponse.<ProgramClassResponse>builder()
                .message("ProgramClass retrieved successfully")
                .result(response)
                .build();
    }

    // API: Lấy danh sách ProgramClass (phân trang)
    @GetMapping
    public ApiResponse<PageDTO<ProgramClassResponse>> getAllProgramClasses(Pageable pageable) {
        Page<ProgramClassResponse> responsePage = programClassService.getAllProgramClasses(pageable);
        PageDTO<ProgramClassResponse> pageDTO = new PageDTO<>(responsePage);
        return ApiResponse.<PageDTO<ProgramClassResponse>>builder()
                .message("ProgramClasses retrieved successfully")
                .result(pageDTO)
                .build();
    }

    // API: Tạo mới ProgramClass
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

    // API: Cập nhật ProgramClass
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

    // API: Xóa ProgramClass theo ID
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<Void> deleteProgramClass(@PathVariable Long id) {
        programClassService.deleteProgramClass(id);
        return ApiResponse.<Void>builder()
                .message("ProgramClass deleted successfully")
                .build();
    }

    // API: Xóa nhiều ProgramClass
    @DeleteMapping("/batch")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<Void> deleteProgramClasses(@RequestBody List<Long> ids) {
        programClassService.deleteProgramClasses(ids);
        return ApiResponse.<Void>builder()
                .message("Selected ProgramClasses deleted successfully")
                .build();
    }
}
