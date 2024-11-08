package com.spkt.librasys.controller;

import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spkt.librasys.service.impl.CloudinaryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/cloudinary")
public class CloudinaryController {

    private final CloudinaryService cloudinaryService;

    public CloudinaryController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    /**
     * Endpoint to upload a file.
     *
     * @param file the file to upload
     * @return the upload result
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            Map uploadResult = cloudinaryService.uploadFile(file, ObjectUtils.emptyMap());
            return ResponseEntity.ok(uploadResult);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading file: " + e.getMessage());
        }
    }

    /**
     * Endpoint to delete a file.
     *
     * @param publicId the public ID of the file to delete
     * @return the deletion result
     */
    @PostMapping("/delete")
    public ResponseEntity<?> deleteFile(@RequestParam("publicId") String publicId) {
        try {
            Map deleteResult = cloudinaryService.deleteFile(publicId);
            return ResponseEntity.ok(deleteResult);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting file: " + e.getMessage());
        }
    }
}
