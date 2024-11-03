package com.spkt.librasys.dto.request.document;

import com.spkt.librasys.entity.enums.DocumentSize;
import com.spkt.librasys.entity.enums.DocumentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    String documentLink;

    String coverImage;

    @NotNull(message = "Size is required")
    DocumentSize size;

    @NotNull(message = "Status is required")
    DocumentStatus status;

    @NotNull(message = "Document type IDs are required")
    Set<Long> documentTypeIds; // Sử dụng Set để tránh trùng lặp
}
