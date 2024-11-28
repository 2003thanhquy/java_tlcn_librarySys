package com.spkt.librasys.service;

import com.spkt.librasys.dto.request.role.RoleCreateRequest;
import com.spkt.librasys.dto.response.role.RoleResponse;
import com.spkt.librasys.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Giao diện RoleService định nghĩa các hành vi liên quan đến việc quản lý vai trò (role) của người dùng.
 */
public interface RoleService {

    /**
     * Kiểm tra xem người dùng có vai trò cụ thể hay không.
     *
     * @param userId ID của người dùng.
     * @param roleName Tên vai trò cần kiểm tra.
     * @return true nếu người dùng có vai trò này, false nếu không.
     */
    boolean hasRole(String userId, String roleName);

    /**
     * Lấy danh sách tất cả các vai trò (phân trang).
     *
     * @param pageable Thông tin phân trang.
     * @return Page chứa danh sách các vai trò.
     */
    Page<RoleResponse> getAllRoles(Pageable pageable);

    /**
     * Kiểm tra xem người dùng có phải là quản trị viên (Admin) hay không.
     *
     * @param user Người dùng cần kiểm tra.
     * @return true nếu người dùng là quản trị viên, false nếu không.
     */
    Boolean isAdmin(User user);

    /**
     * Gán một vai trò cho người dùng.
     *
     * @param userId ID của người dùng cần gán vai trò.
     * @param roleName Tên vai trò cần gán cho người dùng.
     */
    void assignRoleToUser(String userId, String roleName);
}
