package com.spkt.librasys.controller;

import com.spkt.librasys.dto.response.ApiResponse;
import com.spkt.librasys.service.ProgramClassService;
import com.spkt.librasys.service.impl.ProgramClassServiceImpl;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/program-classes")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProgramClassController {

    ProgramClassService programClassService;

    @PostMapping("/upload")
    @RateLimiter(name = "programClassUploadLimiter")
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
}
