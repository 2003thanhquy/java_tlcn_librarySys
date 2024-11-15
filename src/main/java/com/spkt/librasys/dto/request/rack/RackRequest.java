package com.spkt.librasys.dto.request.rack;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RackRequest {

    @NotBlank(message = "Rack number is required")
    private String rackNumber;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be greater than 0")
    private double capacity;

    @NotNull(message = "Shelf ID is required")
    private Long shelfId; // ID của Shelf mà Rack thuộc về
}
