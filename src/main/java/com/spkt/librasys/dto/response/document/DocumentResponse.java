package com.spkt.librasys.dto.response.document;

import com.spkt.librasys.entity.enums.DocumentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocumentResponse {
    long documentId;
    String documentName;
    String isbn;                // Mã ISBN cho tài liệu
    String author;
    String publisher;
    LocalDate publishedDate;
    int pageCount;
    String language;            // Ngôn ngữ của tài liệu
    int quantity;
    int availableCount;         // Số lượng sách có sẵn để mượn
    DocumentStatus status;      // Trạng thái của sách
    String description;
    String documentLink;
    String documentTypeName;
}
