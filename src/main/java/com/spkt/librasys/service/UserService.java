package com.spkt.librasys.service;

import com.spkt.librasys.dto.request.user.ChangePasswordRequest;
import com.spkt.librasys.dto.request.user.UserCreateRequest;
import com.spkt.librasys.dto.request.user.UserUpdateRequest;
import com.spkt.librasys.dto.response.user.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Giao diện UserService định nghĩa các hành vi liên quan đến người dùng trong hệ thống.
 * Các phương thức này bao gồm việc tạo, cập nhật, xóa, và quản lý thông tin người dùng.
 */
public interface UserService {

    /**
     * Lấy thông tin cá nhân của người dùng hiện tại.
     *
     * @return UserResponse chứa thông tin của người dùng hiện tại.
     */
    UserResponse getMyInfo();

    /**
     * Lấy thông tin của người dùng theo ID.
     *
     * @param id ID của người dùng.
     * @return UserResponse chứa thông tin chi tiết của người dùng.
     */
    UserResponse getUserById(String id);

    /**
     * Tạo mới một người dùng trong hệ thống.
     *
     * @param userCreateRequest Dữ liệu yêu cầu tạo người dùng mới.
     * @return UserResponse chứa thông tin của người dùng vừa được tạo.
     */
    UserResponse createUser(UserCreateRequest userCreateRequest);

    /**
     * Lấy danh sách tất cả người dùng, có hỗ trợ phân trang và tìm kiếm theo tên người dùng.
     *
     * @param username Tên người dùng để tìm kiếm (tùy chọn).
     * @param pageable Thông tin phân trang.
     * @return Page<UserResponse> chứa danh sách người dùng và thông tin phân trang.
     */
    Page<UserResponse> getAllUsers(String username, Pageable pageable);

    /**
     * Cập nhật thông tin người dùng.
     *
     * @param id                 ID của người dùng cần cập nhật.
     * @param userUpdateRequest  Dữ liệu mới để cập nhật thông tin người dùng.
     * @return UserResponse chứa thông tin của người dùng sau khi cập nhật.
     */
    UserResponse updateUser(String id, UserUpdateRequest userUpdateRequest);

    /**
     * Xóa người dùng theo ID.
     *
     * @param id ID của người dùng cần xóa.
     */
    void deleteUser(String id);

    /**
     * Thay đổi mật khẩu của người dùng.
     *
     * @param request Chứa thông tin yêu cầu thay đổi mật khẩu.
     */
    void changePassword(ChangePasswordRequest request);

    /**
     * Vô hiệu hóa tài khoản người dùng với lý do cụ thể.
     *
     * @param userId ID của người dùng cần vô hiệu hóa.
     * @param reason Lý do vô hiệu hóa tài khoản.
     */
    void deactivateUser(String userId, String reason);

    /**
     * Kích hoạt lại tài khoản người dùng đã bị vô hiệu hóa.
     *
     * @param userId ID của người dùng cần kích hoạt lại tài khoản.
     */
    void reactivateUser(String userId);

    /**
     * Khóa tài khoản người dùng với lý do cụ thể.
     *
     * @param userId ID của người dùng cần khóa tài khoản.
     * @param reason Lý do khóa tài khoản.
     */
    void lockUser(String userId, String reason);

    /**
     * Mở khóa tài khoản người dùng.
     *
     * @param userId ID của người dùng cần mở khóa tài khoản.
     */
    void unlockUser(String userId);

    /**
     * Xóa nhiều người dùng theo danh sách ID.
     *
     * @param userIds Danh sách ID của người dùng cần xóa.
     */
    void deleteUsersByIds(List<String> userIds);
}
