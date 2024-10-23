package com.spkt.librasys.mapper;

import com.spkt.librasys.dto.request.role.RoleCreateRequest;
import com.spkt.librasys.dto.response.role.RoleResponse;
import com.spkt.librasys.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    Role toRole(RoleCreateRequest request);

    RoleResponse toRoleResponse(Role role);
}
