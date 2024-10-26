package com.spkt.librasys.service.impl;

import com.spkt.librasys.dto.request.role.RoleCreateRequest;
import com.spkt.librasys.dto.response.role.RoleResponse;
import com.spkt.librasys.entity.Role;
import com.spkt.librasys.entity.User;
import com.spkt.librasys.exception.AppException;
import com.spkt.librasys.exception.ErrorCode;
import com.spkt.librasys.mapper.RoleMapper;
import com.spkt.librasys.repository.RoleRepository;
import com.spkt.librasys.repository.access.UserRepository;
import com.spkt.librasys.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleServiceImpl implements RoleService {

    UserRepository userRepository;
    RoleRepository roleRepository;
    RoleMapper roleMapper;

    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public boolean hasRole(String userId, String roleName) {
        return roleRepository.existsByNameAndUsersUserId(roleName,userId);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Page<RoleResponse> getAllRoles(Pageable pageable) {
        return roleRepository.findAll(pageable).map(roleMapper::toRoleResponse);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public RoleResponse createRole(RoleCreateRequest request) {
        Role role = roleMapper.toRole(request);
        Role savedRole = roleRepository.save(role);
        return roleMapper.toRoleResponse(savedRole);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public RoleResponse updateRole(String roleName, RoleCreateRequest request) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        role.setDescription(request.getDescription());
        Role updatedRole = roleRepository.save(role);
        return roleMapper.toRoleResponse(updatedRole);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteRole(String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        roleRepository.delete(role);
    }

    @Override
    public Boolean isAdmin(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));
    }
    @Transactional
    public void assignRoleToUser(String userId, String roleName) {
        // Tìm người dùng
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Tìm vai trò
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND,"Role not found"));

        // Gán vai trò cho người dùng

        var roles = new HashSet<Role>();
        roles.add(role);
        user.setRoles(roles);
        userRepository.save(user);
    }
}