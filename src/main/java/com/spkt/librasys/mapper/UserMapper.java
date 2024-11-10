package com.spkt.librasys.mapper;

import com.spkt.librasys.dto.request.user.UserCreateRequest;
import com.spkt.librasys.dto.request.user.UserUpdateRequest;
import com.spkt.librasys.dto.response.user.UserResponse;
import com.spkt.librasys.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {DepartmentMapper.class, RoleMapper.class})
public interface UserMapper {

    @Mapping(target = "department", ignore = true)
    User toUser(UserCreateRequest request);

    @Mapping(target = "department", source = "department")
    @Mapping(target = "roles", source = "roles")
    UserResponse toUserResponse(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
