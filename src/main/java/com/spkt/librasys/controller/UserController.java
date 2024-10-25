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

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/users")
public class UserController {

    UserService userService;
    VerificationService verificationService;

    @GetMapping ("/{userId}")
    public  ApiResponse<UserResponse> getUser(@PathVariable String userId){
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUserById(userId))
                .build();
    }
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
    @GetMapping("/my-info")
    ApiResponse<UserResponse> getMyInfo() {
        log.info("get my Info");
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }
    @PostMapping
    public ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }
    @PutMapping("/{userId}")
    public ApiResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody @Valid UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(userId, request))
                .build();
    }
    @DeleteMapping("/{userId}")
    public ApiResponse<String> deleteUser(@PathVariable String userId){
        userService.deleteUser(userId);
        return ApiResponse.<String>builder()
                .result("User has been deleted").build();
    }
    // Phương thức để thay đổi mật khẩu
    @PatchMapping("/change-password")
    public ApiResponse<String> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        userService.changePassword(request);
        return ApiResponse.<String>builder()
                .result("Password has been changed successfully")
                .build();
    }
    // Vô hiệu hóa tài khoản người dùng
    @PatchMapping("/{userId}/deactivate")
    public ApiResponse<String> deactivateUser(@PathVariable String userId, @RequestParam(required = false) String reason) {
        userService.deactivateUser(userId, reason);
        return ApiResponse.<String>builder()
                .result("User has been deactivated successfully")
                .build();
    }

    // Kích hoạt lại tài khoản người dùng
    @PatchMapping("/{userId}/reactivate")
    public ApiResponse<String> reactivateUser(@PathVariable String userId) {
        userService.reactivateUser(userId);
        return ApiResponse.<String>builder()
                .result("User has been reactivated successfully")
                .build();
    }

    // Khóa tài khoản người dùng
    @PatchMapping("/{userId}/lock")
    public ApiResponse<String> lockUser(@PathVariable String userId, @RequestParam(required = false) String reason) {
        userService.lockUser(userId, reason);
        return ApiResponse.<String>builder()
                .result("User has been locked successfully")
                .build();
    }

    // Mở khóa tài khoản người dùng
    @PatchMapping("/{userId}/unlock")
    public ApiResponse<String> unlockUser(@PathVariable String userId) {
        userService.unlockUser(userId);
        return ApiResponse.<String>builder()
                .result("User has been unlocked successfully")
                .build();
    }

    @PostMapping("/verify-account")
    public ApiResponse<Void> verifyAccount(@RequestBody VerificationRequest request) {
        boolean isVerified = verificationService.verifyAccount(request);
        return ApiResponse.<Void>builder()
                .message(isVerified?"Account verified successfully":"Invalid or expired verification code")
                .build();
    }
    @PostMapping("/resend-verification")
    public ApiResponse<Void> resendVerificationCode(@RequestParam String email) {
        boolean isResent = verificationService.resendVerificationCode(email);

        return ApiResponse.<Void>builder()
                    .message(isResent?"Verification code resent successfully":"Failed to resend verification code. Please try again later.")
                    .build();

    }

}
