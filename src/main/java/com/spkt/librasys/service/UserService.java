package com.spkt.librasys.service;

import com.spkt.librasys.dto.request.userRequest.UserCreateRequest;
import com.spkt.librasys.dto.request.userRequest.UserUpdateRequest;
import com.spkt.librasys.dto.response.userResponse.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse getMyInfo();
    UserResponse getUserById(String id);
    UserResponse createUser(UserCreateRequest userCreateRequest);
    List<UserResponse> getAllUsers();
    UserResponse updateUser(String id, UserUpdateRequest userUpdateRequest);
    void deleteUser(String id);
}
