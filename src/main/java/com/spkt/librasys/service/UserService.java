package com.spkt.librasys.service;

import com.spkt.librasys.dto.request.userRequest.UserCreateRequest;
import com.spkt.librasys.dto.request.userRequest.UserUpdateRequest;
import com.spkt.librasys.dto.response.userResponse.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    UserResponse getMyInfo();
    UserResponse getUserById(String id);
    UserResponse createUser(UserCreateRequest userCreateRequest);
    Page<UserResponse> getAllUsers(String username, Pageable pageable);  // Thêm phân trang và tìm kiếm
    UserResponse updateUser(String id, UserUpdateRequest userUpdateRequest);
    void deleteUser(String id);
}
