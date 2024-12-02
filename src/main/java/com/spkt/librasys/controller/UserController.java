package com.spkt.librasys.controller;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.request.VerificationRequest;
import com.spkt.librasys.dto.request.user.ChangePasswordRequest;
import com.spkt.librasys.dto.request.user.UserCreateRequest;
import com.spkt.librasys.dto.request.user.UserUpdateRequest;
import com.spkt.librasys.dto.response.ApiResponse;
import com.spkt.librasys.dto.response.user.UserResponse;
import com.spkt.librasys.service.UserService;
import com.spkt.librasys.service.VerificationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller quản lý các yêu cầu liên quan đến người dùng trong hệ thống.
 * Các API này cho phép tạo mới, cập nhật, xóa người dùng, cũng như các chức năng liên quan đến xác minh tài khoản và thay đổi mật khẩu.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/users")
public class UserController {

    UserService userService;

    /**
     * Lấy thông tin người dùng theo ID.
     *
     * @param userId ID của người dùng
     * @return ApiResponse chứa thông tin người dùng
     */
    @GetMapping ("/{userId}")
    public ApiResponse<UserResponse> getUser(@PathVariable String userId){
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUserById(userId))
                .build();
    }

    /**
     * Lấy danh sách tất cả người dùng trong hệ thống với phân trang.
     *
     * @param username (tuỳ chọn) tên người dùng để tìm kiếm
     * @param pageable thông tin phân trang
     * @return ApiResponse chứa danh sách người dùng với phân trang
     */
    @GetMapping
    public ApiResponse<PageDTO<UserResponse>> getAllUsers(
            @RequestParam(required = false) String username,
            Pageable pageable) {
        Page<UserResponse> users = userService.getAllUsers(username, pageable);
        PageDTO<UserResponse> pageDTO = new PageDTO<>(users);
        return ApiResponse.<PageDTO<UserResponse>>builder()
                .result(pageDTO)
                .build();
    }

    /**
     * Lấy thông tin của người dùng hiện tại.
     *
     * @return ApiResponse chứa thông tin người dùng hiện tại
     */
    @GetMapping("/my-info")
    ApiResponse<UserResponse> getMyInfo() {
        log.info("get my Info");
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    /**
     * Tạo mới người dùng trong hệ thống.
     *
     * @param request yêu cầu tạo người dùng mới
     * @return ApiResponse chứa thông tin người dùng mới được tạo
     */
    @PostMapping
    public ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    /**
     * Cập nhật thông tin người dùng.
     *
     * @param userId ID của người dùng cần cập nhật
     * @param request yêu cầu cập nhật thông tin người dùng
     * @return ApiResponse chứa thông tin người dùng sau khi cập nhật
     */
    @PutMapping("/{userId}")
    public ApiResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody @Valid UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(userId, request))
                .build();
    }

    /**
     * Xóa người dùng theo ID.
     *
     * @param userId ID của người dùng cần xóa
     * @return ApiResponse thông báo xóa người dùng thành công
     */
    @DeleteMapping("/{userId}")
    public ApiResponse<String> deleteUser(@PathVariable String userId){
        userService.deleteUser(userId);
        return ApiResponse.<String>builder()
                .result("Người dùng đã được xóa thành công")
                .build();
    }

    /**
     * Thay đổi mật khẩu của người dùng.
     *
     * @param request yêu cầu thay đổi mật khẩu
     * @return ApiResponse thông báo thay đổi mật khẩu thành công
     */
    @PatchMapping("/change-password")
    public ApiResponse<String> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        userService.changePassword(request);
        return ApiResponse.<String>builder()
                .result("Mật khẩu đã được thay đổi thành công")
                .build();
    }

    /**
     * Vô hiệu hóa người dùng.
     *
     * @param userId ID của người dùng cần vô hiệu hóa
     * @param reason lý do vô hiệu hóa (tuỳ chọn)
     * @return ApiResponse thông báo người dùng đã bị vô hiệu hóa
     */
    @PatchMapping("/{userId}/deactivate")
    public ApiResponse<String> deactivateUser(@PathVariable String userId, @RequestParam(required = false) String reason) {
        userService.deactivateUser(userId, reason);
        return ApiResponse.<String>builder()
                .result("Người dùng đã bị vô hiệu hóa thành công")
                .build();
    }

    /**
     * Kích hoạt lại người dùng.
     *
     * @param userId ID của người dùng cần kích hoạt lại
     * @return ApiResponse thông báo người dùng đã được kích hoạt lại
     */
    @PatchMapping("/{userId}/reactivate")
    public ApiResponse<String> reactivateUser(@PathVariable String userId) {
        userService.reactivateUser(userId);
        return ApiResponse.<String>builder()
                .result("Người dùng đã được kích hoạt lại thành công")
                .build();
    }

    /**
     * Khóa người dùng.
     *
     * @param userId ID của người dùng cần khóa
     * @param reason lý do khóa người dùng (tuỳ chọn)
     * @return ApiResponse thông báo người dùng đã bị khóa
     */
    @PatchMapping("/{userId}/lock")
    public ApiResponse<String> lockUser(@PathVariable String userId, @RequestParam(required = false) String reason) {
        userService.lockUser(userId, reason);
        return ApiResponse.<String>builder()
                .result("Người dùng đã bị khóa thành công")
                .build();
    }

    /**
     * Mở khóa người dùng.
     *
     * @param userId ID của người dùng cần mở khóa
     * @return ApiResponse thông báo người dùng đã được mở khóa
     */
    @PatchMapping("/{userId}/unlock")
    public ApiResponse<String> unlockUser(@PathVariable String userId) {
        userService.unlockUser(userId);
        return ApiResponse.<String>builder()
                .result("Người dùng đã được mở khóa thành công")
                .build();
    }
    /**
     * Xóa nhiều người dùng theo danh sách ID.
     *
     * @param userIds danh sách các ID người dùng cần xóa
     * @return ApiResponse thông báo xóa nhiều người dùng thành công
     */
    @DeleteMapping("/batch")
    public ApiResponse<Void> deleteUsers(@RequestBody List<String> userIds) {
        userService.deleteUsersByIds(userIds);
        return ApiResponse.<Void>builder()
                .message("Người dùng đã được xóa thành công")
                .build();
    }
}
