package com.spkt.librasys.dto.response.documentResponse;

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
    String author;
    String publisher;
    LocalDate publishedDate;
    int pageCount;
    int quantity;
    String description;
    String documentLink;
    String documentTypeName;
}