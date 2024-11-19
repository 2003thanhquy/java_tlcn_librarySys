package com.spkt.librasys.dto.request.document;

import com.spkt.librasys.entity.enums.DocumentSize;
import com.spkt.librasys.entity.enums.DocumentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocumentUpdateRequest {

    @NotBlank(message = "Document name is required")
    String documentName;

    @NotBlank(message = "ISBN is required")
    String isbn;

    @NotBlank(message = "Author is required")
    String author;

    String publisher;

    LocalDate publishedDate;

    @NotNull(message = "Page count is required")
    Integer pageCount;

    String language;

    BigDecimal price;

    String description;

    @NotNull(message = "Size is required")
    DocumentSize size;

    @NotNull(message = "Status is required")
    DocumentStatus status;

    @NotNull(message = "Document type IDs are required")
    Set<Long> documentTypeIds;

    // Trường thêm để hỗ trợ cập nhật file ảnh và PDF
    MultipartFile image; // Ảnh bìa
    MultipartFile pdfFile; // File PDF
}
