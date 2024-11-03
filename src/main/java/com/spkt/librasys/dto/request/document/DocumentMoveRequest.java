package com.spkt.librasys.dto.request.document;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DocumentMoveRequest {
    @NotNull(message = "Document ID cannot be null")
    Long documentId;

    @NotNull(message = "Warehouse ID cannot be null")
    Long warehouseId;

    @NotNull(message = "Rack ID cannot be null")
    Long rackId;

    @Min(value = 1, message = "Quantity must be at least 1")
    int quantity;
}