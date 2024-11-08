package com.spkt.librasys.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public Map<String, Object> uploadFile(MultipartFile file,Map options) throws IOException {
//        Map options = ObjectUtils.asMap(
//            "folder", "document",
//        "overwrite", true,
//                "public_id", "1"
//        );

        return cloudinary.uploader().upload(file.getBytes(), options);
    }

    public Map<String, Object> deleteFile(String publicId) throws IOException {
        return cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    // Các phương thức khác nếu cần
}
