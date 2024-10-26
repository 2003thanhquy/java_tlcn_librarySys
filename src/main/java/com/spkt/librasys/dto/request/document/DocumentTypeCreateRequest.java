package com.spkt.librasys.dto.request.document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DocumentTypeCreateRequest {
    @NotBlank(message = "Tên loại tài liệu không được để trống")
    @Size(max = 100, message = "Tên loại tài liệu không được vượt quá 100 ký tự")
    private String typeName;

    @Size(max = 100, message = "Mô tả không được vượt quá 500 ký tự")
    private String description;
}