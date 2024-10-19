package com.spkt.librasys.dto.request.document;

import com.spkt.librasys.entity.enums.DocumentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
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
    @Min(value = 0, message = "Quantity must be greater than 0")
    int availableCount;


    @NotBlank(message = "Location code cannot be blank")
    String locationCode; // Mã vị trí trong thư viện

    String description;  // Mô tả tài liệu (có thể không bắt buộc)

    String coverImage;    // Đường dẫn ảnh bìa

    String documentLink;  // Đường dẫn tới tài liệu điện tử (nếu có)
    BigDecimal price;
    int maxLoanDays;;

    @NotNull(message = "Document type ID cannot be null")
    Long documentTypeId;

    @NotNull(message = "Status cannot be null")
    DocumentStatus status;
}
