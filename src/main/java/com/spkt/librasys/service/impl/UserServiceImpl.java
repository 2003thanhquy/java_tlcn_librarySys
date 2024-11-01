package com.spkt.librasys.service.impl;

import com.spkt.librasys.constant.PredefinedRole;
import com.spkt.librasys.dto.request.user.ChangePasswordRequest;
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
import com.spkt.librasys.service.*;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.service.SecurityService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    SecurityContextService securityContextService;
    RoleService roleService;
    VerificationService verificationService;
    @Override
    public UserResponse getMyInfo() {
        User currentUser =  securityContextService.getCurrentUser(); // Lấy thông tin người dùng hiện tại từ SecurityContextHolder
        if(currentUser == null)  throw new AppException(ErrorCode.UNAUTHORIZED);

        return userMapper.toUserResponse(currentUser);
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
        verificationService.verificationCode(user.getUsername());
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
        User currentUser =  securityContextService.getCurrentUser(); // Lấy thông tin người dùng hiện tại từ SecurityContextHolder
        if(currentUser == null)  throw new AppException(ErrorCode.UNAUTHORIZED);
        // Tìm kiếm người dùng cần cập nhật thông qua ID
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        // Kiểm tra quyền và xác định hành động cần thực hiện dựa trên vai trò
        if (roleService.isAdmin(currentUser)) {
            // Quản trị viên có thể cập nhật mọi thông tin, bao gồm cả vai trò của người dùng
//            if (request.getRoles() != null && !request.getRoles().isEmpty()) {
//                var roles = roleRepository.findAllById(request.getRoles());
//                user.setRoles(new HashSet<>(roles));
//            }
        } else if (!currentUser.getUserId().equals(id)) {
            // Nếu không phải là quản trị viên và không phải chính người dùng, ném ngoại lệ
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        // Ánh xạ các thông tin còn lại từ UserUpdateRequest vào thực thể User
        userMapper.updateUser(user, request);

        // Lưu lại người dùng sau khi cập nhật
        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }



    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.getIsActive() == User.Status.DELETED) {
            throw new AppException(ErrorCode.USER_ALREADY_DELETED);
        }

        user.setIsActive(User.Status.DELETED);
        userRepository.save(user);
    }
    @Override
    public void changePassword(ChangePasswordRequest request) {
        // Lấy thông tin người dùng hiện tại
        User currentUser =  securityContextService.getCurrentUser();
        if(currentUser == null)  throw new AppException(ErrorCode.UNAUTHORIZED);

        // Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(request.getOldPassword(), currentUser.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS, "Mật khẩu cũ không chính xác");
        }

        // Kiểm tra mật khẩu mới và xác nhận mật khẩu mới
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Mật khẩu mới không khớp với xác nhận mật khẩu");
        }

        // Cập nhật mật khẩu mới
        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);
    }
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deactivateUser(String userId, String reason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setIsActive(User.Status.DEACTIVATED);
        user.setDeactivatedAt(LocalDateTime.now());
        user.setDeactivationReason(reason);
        userRepository.save(user);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void reactivateUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (user.getIsActive() != User.Status.DEACTIVATED) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "User is not in a deactivated state.");
        }
        user.setIsActive(User.Status.ACTIVE);
        user.setReactivatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void lockUser(String userId, String reason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setIsActive(User.Status.LOCKED);
        user.setLockedAt(LocalDateTime.now());
        user.setLockReason(reason);
        user.setLockCount(user.getLockCount() + 1);
        userRepository.save(user);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void unlockUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (user.getIsActive() != User.Status.LOCKED) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "User is not in a locked state.");
        }
        user.setIsActive(User.Status.ACTIVE);
        user.setLockedAt(null);
        user.setLockReason(null);
        userRepository.save(user);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteUsersByIds(List<String> userIds) {
        List<User> users = userRepository.findAllById(userIds);

        // Cập nhật trạng thái DELETED cho tất cả các user
        users.forEach(user -> user.setIsActive(User.Status.DELETED));

        // Lưu lại danh sách đã cập nhật
        userRepository.saveAll(users);
    }

}
