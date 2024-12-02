package com.spkt.librasys.dto.request.document;

import lombok.Data;

@Data
public class DocumentSearchRequest {
    private String documentName;
    private Long[] documentTypeIds;
    private Long[] courseIds;
}
