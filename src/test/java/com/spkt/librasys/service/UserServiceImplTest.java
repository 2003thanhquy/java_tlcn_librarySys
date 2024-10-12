package com.spkt.librasys.service.impl;

import com.spkt.librasys.constant.PredefinedRole;
import com.spkt.librasys.dto.request.userRequest.UserCreateRequest;
import com.spkt.librasys.dto.request.userRequest.UserUpdateRequest;
import com.spkt.librasys.dto.response.userResponse.UserResponse;
import com.spkt.librasys.entity.Role;
import com.spkt.librasys.entity.User;
import com.spkt.librasys.exception.AppException;
import com.spkt.librasys.mapper.UserMapper;
import com.spkt.librasys.repository.access.RoleRepository;
import com.spkt.librasys.repository.access.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // Thiết lập SecurityContext với Authentication
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Test
    public void testGetMyInfo_Success() {
        String username = "testuser";
        User user = User.builder().username(username).build();
        UserResponse userResponse = UserResponse.builder().username(username).build();

        // Giả lập security context để trả về username
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse response = userService.getMyInfo();

        // Xuất thông tin để người dùng kiểm tra
        System.out.println("Test Get My Info Success - User Info: " + response);

        assertNotNull(response);
        assertEquals(username, response.getUsername());
    }

    @Test
    public void testGetUserById_Success() {
        String userId = "testId";
        User user = User.builder().userId(userId).username("testuser").build();
        UserResponse userResponse = UserResponse.builder().userId(userId).username("testuser").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse response = userService.getUserById(userId);

        // Xuất thông tin để người dùng kiểm tra
        System.out.println("Test Get User By ID Success - User Info: " + response);

        assertNotNull(response);
        assertEquals(userId, response.getUserId());
    }

    @Test
    public void testGetUserById_NotFound() {
        when(userRepository.findById("invalidId")).thenReturn(Optional.empty());

        Exception exception = assertThrows(AppException.class, () -> userService.getUserById("invalidId"));
        System.out.println("Test Get User By ID Not Found - Exception: " + exception.getMessage());
    }

    @Test
    public void testCreateUser_Success() {
        UserCreateRequest request = UserCreateRequest.builder()
                .username("newuser")
                .password("password123")
                .build();
        User user = User.builder()
                .username("newuser")
                .password("hashedPassword")
                .build();
        Role userRole = new Role();
        userRole.setName(PredefinedRole.USER_ROLE);

        when(userMapper.toUser(request)).thenReturn(user);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashedPassword");  // Mock hành vi encode
        when(roleRepository.findById(PredefinedRole.USER_ROLE)).thenReturn(Optional.of(userRole));
        when(userRepository.save(user)).thenReturn(user);
        UserResponse userResponse = UserResponse.builder().username("newuser").build();
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse response = userService.createUser(request);

        // Xuất thông tin để người dùng kiểm tra
        System.out.println("Test Create User Success - User Info: " + response);

        assertNotNull(response);
        assertEquals("newuser", response.getUsername());
    }

    @Test
    public void testCreateUser_DuplicateUser() {
        UserCreateRequest request = UserCreateRequest.builder()
                .username("newuser")
                .password("password123")
                .build();
        User user = User.builder().username("newuser").build();

        when(userMapper.toUser(request)).thenReturn(user);
        when(userRepository.save(user)).thenThrow(DataIntegrityViolationException.class);

        Exception exception = assertThrows(AppException.class, () -> userService.createUser(request));
        System.out.println("Test Create User Duplicate - Exception: " + exception.getMessage());
    }

    @Test
    public void testGetAllUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        User user = User.builder().userId("testId").username("testuser").build();
        Page<User> users = new PageImpl<>(Collections.singletonList(user));
        UserResponse userResponse = UserResponse.builder().userId("testId").username("testuser").build();

        when(userRepository.findAll(pageable)).thenReturn(users);
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        Page<UserResponse> response = userService.getAllUsers(null, pageable);

        // Xuất thông tin để người dùng kiểm tra
        System.out.println("Test Get All Users - Page Info: " + response.getContent());

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals("testuser", response.getContent().get(0).getUsername());
    }

    @Test
    public void testUpdateUser_Success() {
        String userId = "testId";
        UserUpdateRequest request = UserUpdateRequest.builder()
                .firstName("UpdatedFirstName")
                .lastName("UpdatedLastName")
                .build();
        User user = User.builder().userId(userId).username("testuser").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.updateUser(userId, request);

        // Xuất thông tin sau khi cập nhật
        System.out.println("Test Update User Success - Updated User: " + user);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testUpdateUser_NotFound() {
        when(userRepository.findById("invalidId")).thenReturn(Optional.empty());
        UserUpdateRequest request = new UserUpdateRequest();
        Exception exception = assertThrows(AppException.class, () -> userService.updateUser("invalidId", request));
        System.out.println("Test Update User Not Found - Exception: " + exception.getMessage());
    }
}
