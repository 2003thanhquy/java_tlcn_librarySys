package com.spkt.librasys.dto.request.displayzone;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DisplayZoneRequest {

    @NotBlank(message = "Zone name is required")
    private String zoneName;
}
