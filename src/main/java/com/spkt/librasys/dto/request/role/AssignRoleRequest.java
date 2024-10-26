package com.spkt.librasys.dto.request.role;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignRoleRequest {
    
    @NotBlank(message = "Role name không được để trống")
    private String roleName;
}
