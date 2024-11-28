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

/**
 * Controller xử lý các yêu cầu liên quan đến vai trò người dùng trong hệ thống.
 * Các API này cho phép kiểm tra vai trò, gán vai trò cho người dùng và lấy thông tin các vai trò hiện có.
 */
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/roles")
public class RoleController {

    RoleService roleService;

    /**
     * Kiểm tra xem người dùng có vai trò cụ thể hay không.
     *
     * @param roleName tên vai trò cần kiểm tra
     * @param userId ID của người dùng cần kiểm tra vai trò
     * @return ApiResponse chứa kết quả kiểm tra vai trò (true nếu người dùng có vai trò, false nếu không có)
     */
    @GetMapping("/has-role/{roleName}")
    @PreAuthorize("hasRole('AMDIN')")
    public ApiResponse<Boolean> hasRole(@PathVariable String roleName, @RequestParam String userId) {
        boolean hasRole = roleService.hasRole(userId, roleName);
        return ApiResponse.<Boolean>builder()
                .message("Kiểm tra vai trò hoàn tất thành công")
                .result(hasRole)
                .build();
    }

    /**
     * Gán vai trò cho người dùng.
     *
     * @param userId ID của người dùng cần gán vai trò
     * @param request yêu cầu gán vai trò, bao gồm tên vai trò cần gán
     * @return ApiResponse thông báo gán vai trò thành công
     */
    @PatchMapping("/users/{userId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> assignRoleToUser(
            @PathVariable @NotBlank(message = "User ID không được để trống") String userId,
            @Valid @RequestBody AssignRoleRequest request) {
        roleService.assignRoleToUser(userId, request.getRoleName());
        return ApiResponse.<Void>builder()
                .message("Gán vai trò cho người dùng thành công")
                .build();
    }

    /**
     * Lấy tất cả các vai trò trong hệ thống.
     *
     * @param pageable thông tin phân trang
     * @return ApiResponse chứa danh sách các vai trò trong hệ thống
     */
    @GetMapping
    @PreAuthorize("hasRole('AMDIN')")
    public ApiResponse<PageDTO<RoleResponse>> getAllRoles(Pageable pageable) {
        Page<RoleResponse> roles = roleService.getAllRoles(pageable);
        PageDTO<RoleResponse> pageDTO = new PageDTO<>(roles);
        return ApiResponse.<PageDTO<RoleResponse>>builder()
                .message("Lấy danh sách vai trò thành công")
                .result(pageDTO)
                .build();
    }
}
