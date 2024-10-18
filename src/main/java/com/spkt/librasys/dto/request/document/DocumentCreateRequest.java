package com.spkt.librasys.dto.request.document;

import com.spkt.librasys.entity.enums.DocumentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocumentCreateRequest {

    @NotBlank(message = "Document name cannot be blank")
    String documentName;

    @NotBlank(message = "ISBN cannot be blank")
    String isbn;

    @NotBlank(message = "Author cannot be blank")
    String author;

    @NotBlank(message = "Publisher cannot be blank")
    String publisher;

    @NotNull(message = "Published date cannot be null")
    LocalDate publishedDate;

    @Min(value = 1, message = "Page count must be greater than 0")
    int pageCount;

    @NotBlank(message = "Language cannot be blank")
    String language;

    @Min(value = 1, message = "Quantity must be greater than 0")
    int quantity;

    String description;

    String documentLink;

    @NotNull(message = "Document type ID cannot be null")
    Long documentTypeId;

    @NotNull(message = "Status cannot be null")
    DocumentStatus status;
}
