package com.spkt.librasys.dto.response.document;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentTypeResponse {
    private Long documentTypeId;
    private String typeName;
}