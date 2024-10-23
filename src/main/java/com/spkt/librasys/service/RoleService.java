package com.spkt.librasys.service;

import com.spkt.librasys.dto.request.role.RoleCreateRequest;
import com.spkt.librasys.dto.response.role.RoleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoleService {
    boolean hasRole(String userId, String roleName);
    Page<RoleResponse> getAllRoles(Pageable pageable);
    RoleResponse createRole(RoleCreateRequest request);
    RoleResponse updateRole(String roleName, RoleCreateRequest request);
    void deleteRole(String roleName);
}
