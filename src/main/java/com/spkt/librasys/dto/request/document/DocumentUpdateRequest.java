package com.spkt.librasys.dto.request.document;

import com.spkt.librasys.entity.enums.DocumentStatus;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocumentUpdateRequest {
    @NotBlank(message = "DOCUMENT_NAME_CANNOT_BE_BLANK")
    String documentName;

    @NotBlank(message = "ISBN_CANNOT_BE_BLANK")
    String isbn;  // Mã ISBN cho tài liệu, kiểm tra không được trống

    @NotBlank(message = "AUTHOR_CANNOT_BE_BLANK")
    String author;

    String publisher;

    @PastOrPresent(message = "PUBLISHED_DATE_MUST_BE_IN_THE_PAST_OR_PRESENT")
    LocalDate publishedDate;  // Ngày xuất bản, không thể là ngày trong tương lai

    @Positive(message = "PAGE_COUNT_MUST_BE_POSITIVE")
    int pageCount;  // Số trang của tài liệu phải lớn hơn 0

    @NotBlank(message = "LANGUAGE_CANNOT_BE_BLANK")
    String language;  // Ngôn ngữ của tài liệu

    @PositiveOrZero(message = "QUANTITY_MUST_BE_NON_NEGATIVE")
    int quantity;  // Số lượng tài liệu, không được âm

    String description;

    String documentLink;

    @NotNull(message = "DOCUMENT_TYPE_ID_CANNOT_BE_NULL")
    Long documentTypeId;

    @NotNull(message = "DOCUMENT_STATUS_CANNOT_BE_NULL")
    DocumentStatus status;  // Trạng thái của tài liệu (VD: Còn hàng, Đang mượn)
}
