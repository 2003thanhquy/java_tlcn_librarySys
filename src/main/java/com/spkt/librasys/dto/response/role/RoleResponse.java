    package com.spkt.librasys.dto.response.role;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoleResponse {
    private String name;
    private String description;
}
