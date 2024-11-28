package com.spkt.librasys.controller;

import com.cloudinary.utils.ObjectUtils;
import com.spkt.librasys.service.impl.CloudinaryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Controller để xử lý các yêu cầu liên quan đến việc tải lên và xóa tệp từ Cloudinary.
 * Cung cấp các API để tải lên và xóa tệp trên dịch vụ Cloudinary.
 */
@RestController
@RequestMapping("/api/v1/cloudinary")
public class CloudinaryController {

    private final CloudinaryService cloudinaryService;

    /**
     * Constructor để khởi tạo CloudinaryService.
     *
     * @param cloudinaryService Dịch vụ để xử lý các thao tác tải lên và xóa tệp từ Cloudinary.
     */
    public CloudinaryController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    /**
     * Endpoint để tải lên một tệp lên Cloudinary.
     *
     * @param file Tệp cần tải lên.
     * @return Kết quả tải lên tệp dưới dạng ResponseEntity, chứa thông tin về tệp đã tải lên hoặc thông báo lỗi.
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Gọi dịch vụ Cloudinary để tải lên tệp
            Map uploadResult = cloudinaryService.uploadFile(file, ObjectUtils.emptyMap());
            // Trả về kết quả tải lên thành công
            return ResponseEntity.ok(uploadResult);
        } catch (IOException e) {
            // Trả về lỗi nếu có lỗi trong quá trình tải lên
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading file: " + e.getMessage());
        }
    }

    /**
     * Endpoint để xóa một tệp từ Cloudinary.
     *
     * @param publicId ID công khai của tệp cần xóa.
     * @return Kết quả xóa tệp dưới dạng ResponseEntity, chứa thông tin về việc xóa tệp hoặc thông báo lỗi.
     */
    @PostMapping("/delete")
    public ResponseEntity<?> deleteFile(@RequestParam("publicId") String publicId) {
        try {
            // Gọi dịch vụ Cloudinary để xóa tệp
            Map deleteResult = cloudinaryService.deleteFile(publicId);
            // Trả về kết quả xóa thành công
            return ResponseEntity.ok(deleteResult);
        } catch (IOException e) {
            // Trả về lỗi nếu có lỗi trong quá trình xóa tệp
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting file: " + e.getMessage());
        }
    }
}
