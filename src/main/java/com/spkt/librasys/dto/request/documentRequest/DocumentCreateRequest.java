package com.spkt.librasys.dto.request.documentRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocumentCreateRequest {
    String documentName;
    String author;
    String publisher;
    LocalDate publishedDate;
    int pageCount;
    int quantity;
    String description;
    String documentLink;
    Long documentTypeId;
}