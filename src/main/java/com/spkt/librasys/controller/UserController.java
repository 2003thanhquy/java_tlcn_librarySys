package com.spkt.librasys.controller;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.request.user.UserCreateRequest;
import com.spkt.librasys.dto.request.user.UserUpdateRequest;
import com.spkt.librasys.dto.response.ApiResponse;
import com.spkt.librasys.dto.response.user.UserResponse;
import com.spkt.librasys.service.UserService;
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
}
