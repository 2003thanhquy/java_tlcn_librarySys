package com.spkt.librasys.mapper;

import com.spkt.librasys.dto.request.UserRequest;
import com.spkt.librasys.dto.request.userRequest.UserCreateRequest;
import com.spkt.librasys.dto.request.userRequest.UserUpdateRequest;
import com.spkt.librasys.dto.response.userResponse.UserResponse;
import com.spkt.librasys.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreateRequest request);

    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);

}
