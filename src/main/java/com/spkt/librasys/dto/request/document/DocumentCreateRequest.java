package com.spkt.librasys.dto.request.document;

import com.spkt.librasys.entity.enums.DocumentSize;
import com.spkt.librasys.entity.enums.DocumentStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentCreateRequest {

    @NotBlank(message = "ISBN is required")
    private String isbn;

    @NotBlank(message = "Document name is required")
    private String documentName;

    @NotBlank(message = "Author is required")
    private String author;

    private String publisher;

    private LocalDate publishedDate;

    @Min(value = 1, message = "Page count must be at least 1")
    private int pageCount;

    private String language;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    // Số lượng sách có sẵn để mượn
    @Min(value = 0, message = "Available count cannot be negative")
    private int availableCount;

    // Trạng thái của sách (AVAILABLE, CHECKED_OUT, etc.)
    @NotNull(message = "Status is required")
    private DocumentStatus status;

    // Mô tả ngắn về tài liệu
    private String description;

    // Giá của tài liệu
    private BigDecimal price;

    // Thuộc tính kích thước của tài liệu
    @NotNull(message = "Size is required")
    private DocumentSize size;

    @NotNull(message = "Document type IDs are required")
    private Set<Long> documentTypeIds;

    //@NotNull(message = "Course IDs are required")
    private Set<Long> courseIds;

    @NotNull(message = "Warehouse ID is required")
    private Long warehouseId;

    // Thêm trường MultipartFile cho coverImage
    private MultipartFile image;

    // Đường dẫn tới tài liệu điện tử (nếu có)
    private MultipartFile pdfFile; // File PDF

}
