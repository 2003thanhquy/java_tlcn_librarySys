package com.spkt.librasys.dto.response.document;

import com.spkt.librasys.entity.DocumentLocation;
import com.spkt.librasys.entity.DocumentType;
import com.spkt.librasys.entity.enums.DocumentSize;
import com.spkt.librasys.entity.enums.DocumentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {
    private Long documentId;
    private String isbn;
    private String documentName;
    private String author;
    private String publisher;
    private LocalDate publishedDate;
    private int pageCount;
    private String language;
    private int quantity;
    private int availableCount;
    private DocumentStatus status;
    private String description;
    private String coverImage;
    private String documentLink;
    private BigDecimal price;
    private DocumentSize size;
    private Set<DocumentType> documentTypes;
    private Set<DocumentLocation> documentLocations;
    // Thêm các trường khác nếu cần
}
