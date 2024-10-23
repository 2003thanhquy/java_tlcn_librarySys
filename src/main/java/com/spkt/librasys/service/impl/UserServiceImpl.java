package com.spkt.librasys.service.impl;

import com.spkt.librasys.constant.PredefinedRole;
import com.spkt.librasys.dto.request.user.UserCreateRequest;
import com.spkt.librasys.dto.request.user.UserUpdateRequest;
import com.spkt.librasys.dto.response.user.UserResponse;
import com.spkt.librasys.entity.Role;
import com.spkt.librasys.entity.User;
import com.spkt.librasys.exception.AppException;
import com.spkt.librasys.exception.ErrorCode;
import com.spkt.librasys.mapper.UserMapper;
import com.spkt.librasys.repository.RoleRepository;
import com.spkt.librasys.repository.access.UserRepository;
import com.spkt.librasys.service.AuthenticationService;
import com.spkt.librasys.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    AuthenticationService authenticationService;
    @Override
    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return userMapper.toUserResponse(user);
    }
    @PreAuthorize("hasRole('ROLE_' + @roleProvider.ADMIN_ROLE)")
    @Override
    public UserResponse getUserById(String id) {
        return userMapper.toUserResponse(
                userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)));
    }

    @Override
    public UserResponse createUser(UserCreateRequest request) {
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);

        user.setRoles(roles);

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.DUPLICATE_USER);
        }
        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasRole('ROLE_' + @roleProvider.ADMIN_ROLE)")
    @Override
    public Page<UserResponse> getAllUsers(String username, Pageable pageable) {
        Page<User> users;

        // Nếu người dùng có nhập username để tìm kiếm
        if (username != null && !username.trim().isEmpty()) {
            users = userRepository.findByUsernameContainingIgnoreCase(username, pageable);
        } else {
            users = userRepository.findAll(pageable);
        }
        return users.map(userMapper::toUserResponse);
    }

    @Transactional
    @Override
//    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public UserResponse updateUser(String id, UserUpdateRequest request) {
        User currentUser = authenticationService.getCurrentUser(); // Lấy thông tin người dùng hiện tại từ SecurityContextHolder
        if(currentUser == null)  throw new AppException(ErrorCode.UNAUTHORIZED);
        // Tìm kiếm người dùng cần cập nhật thông qua ID
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        currentUser.getRoles().forEach(role -> {
            System.out.println("name Role :" + role.getName());
        });
        // Kiểm tra quyền và xác định hành động cần thực hiện dựa trên vai trò
        if (authenticationService.isAdmin(currentUser)) {
            // Quản trị viên có thể cập nhật mọi thông tin, bao gồm cả vai trò của người dùng
            if (request.getRoles() != null && !request.getRoles().isEmpty()) {
                var roles = roleRepository.findAllById(request.getRoles());
                user.setRoles(new HashSet<>(roles));
            }
        } else if (!currentUser.getUserId().equals(id)) {
            // Nếu không phải là quản trị viên và không phải chính người dùng, ném ngoại lệ
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Cập nhật mật khẩu nếu có
        if (request.getPassword() != null) {
            request.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Ánh xạ các thông tin còn lại từ UserUpdateRequest vào thực thể User
        userMapper.updateUser(user, request);

        // Lưu lại người dùng sau khi cập nhật
        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }



    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(String id) {
        //userRepository.deleteById(id);
    }
}
