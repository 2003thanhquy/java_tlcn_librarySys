package com.spkt.librasys.dto.request.document;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocumentQuantityUpdateRequest {

    @NotNull(message = "Document ID is required")
    Long documentId;

    @NotNull(message = "Location ID is required") // Có thể là warehouseId hoặc rackId
    Long locationId;

    @NotNull(message = "Quantity change is required")
    Integer quantityChange;

    @NotNull(message = "Location type is required")
    LocationType locationType; // ENUM: WAREHOUSE hoặc RACK

    public enum LocationType {
        WAREHOUSE,
        RACK
    }
}
