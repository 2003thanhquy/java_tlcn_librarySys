package com.spkt.librasys.controller;

import com.spkt.librasys.dto.request.role.AssignRoleRequest;
import com.spkt.librasys.dto.response.ApiResponse;
import com.spkt.librasys.dto.response.role.RoleResponse;
import com.spkt.librasys.dto.request.role.RoleCreateRequest;
import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.service.RoleService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/roles")
public class RoleController {

    RoleService roleService;

    // Kiểm tra xem người dùng có vai trò cụ thể hay không
    @GetMapping("/has-role/{roleName}")
    @PreAuthorize("hasRole('AMDIN')")
    public ApiResponse<Boolean> hasRole(@PathVariable String roleName, @RequestParam String userId) {
        boolean hasRole = roleService.hasRole(userId, roleName);
        return ApiResponse.<Boolean>builder()
                .message("Role check completed successfully")
                .result(hasRole)
                .build();
    }
    @PatchMapping("/users/{userId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> assignRoleToUser(
            @PathVariable @NotBlank(message = "User ID không được để trống") String userId,
            @Valid @RequestBody AssignRoleRequest request) {
        roleService.assignRoleToUser(userId, request.getRoleName());
        return ApiResponse.<Void>builder()
                .message("Role assigned to user successfully")
                .build();
    }

    // Lấy tất cả các vai trò trong hệ thống
    @GetMapping
    @PreAuthorize("hasRole('AMDIN')")
    public ApiResponse<PageDTO<RoleResponse>> getAllRoles(Pageable pageable) {
        Page<RoleResponse> roles = roleService.getAllRoles(pageable);
        PageDTO<RoleResponse> pageDTO = new PageDTO<>(roles);
        return ApiResponse.<PageDTO<RoleResponse>>builder()
                .message("Roles retrieved successfully")
                .result(pageDTO)
                .build();
    }

//    // Tạo một vai trò mới
//    @PostMapping
//    public ApiResponse<RoleResponse> createRole(@RequestBody RoleCreateRequest request) {
//        RoleResponse role = roleService.createRole(request);
//        return ApiResponse.<RoleResponse>builder()
//                .message("Role created successfully")
//                .result(role)
//                .build();
//    }
//
//    // Cập nhật thông tin của một vai trò
//    @PutMapping("/{roleName}")
//    public ApiResponse<RoleResponse> updateRole(@PathVariable String roleName, @RequestBody RoleCreateRequest request) {
//        RoleResponse role = roleService.updateRole(roleName, request);
//        return ApiResponse.<RoleResponse>builder()
//                .message("Role updated successfully")
//                .result(role)
//                .build();
//    }
//
//    // Xóa một vai trò khỏi hệ thống
//    @DeleteMapping("/{roleName}")
//    public ApiResponse<Void> deleteRole(@PathVariable String roleName) {
//        roleService.deleteRole(roleName);
//        return ApiResponse.<Void>builder()
//                .message("Role deleted successfully")
//                .build();
//    }
}