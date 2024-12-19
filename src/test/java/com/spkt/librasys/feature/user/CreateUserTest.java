package com.spkt.librasys.feature.user;

import com.spkt.librasys.dto.request.user.UserCreateRequest;
import com.spkt.librasys.dto.response.user.UserResponse;
import com.spkt.librasys.entity.Department;
import com.spkt.librasys.entity.Role;
import com.spkt.librasys.entity.User;
import com.spkt.librasys.exception.AppException;
import com.spkt.librasys.mapper.UserMapper;
import com.spkt.librasys.repository.DepartmentRepository;
import com.spkt.librasys.repository.RoleRepository;
import com.spkt.librasys.repository.access.UserRepository;
import com.spkt.librasys.service.VerificationService;
import com.spkt.librasys.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateUserTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private VerificationService verificationService;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserCreateRequest userCreateRequest;
    private User mockUser;
    private Role mockRole;
    private Department mockDepartment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Khởi tạo yêu cầu tạo người dùng
        userCreateRequest = new UserCreateRequest();
        userCreateRequest.setUsername("21110622@domain.com");
        userCreateRequest.setPassword("password123");

        // Khởi tạo đối tượng mock cho User, Role và Department
        mockUser = new User();
        mockUser.setUsername(userCreateRequest.getUsername());
        mockUser.setPassword(userCreateRequest.getPassword());

        mockRole = new Role();
        mockRole.setName("USER");

        mockDepartment = new Department();
        mockDepartment.setDepartmentCodeId(1L);

        // Mock phương thức của các dependency
        when(userMapper.toUser(userCreateRequest)).thenReturn(mockUser);
        when(userRepository.save(mockUser)).thenReturn(mockUser);
        when(roleRepository.findById("USER")).thenReturn(java.util.Optional.of(mockRole));
        when(departmentRepository.findByDepartmentCodeId(1L)).thenReturn(java.util.Optional.of(mockDepartment));
        when(passwordEncoder.encode(userCreateRequest.getPassword())).thenReturn("encodedPassword123");
    }

    @Test
    void testCreateUser_Success() {
        // Gọi phương thức createUser
        UserResponse response = userService.createUser(userCreateRequest);

        // Kiểm tra phản hồi
        assertNotNull(response);
        assertEquals(mockUser.getUsername(), response.getUsername());
        assertEquals("encodedPassword123", mockUser.getPassword());
        assertTrue(mockUser.getRoles().contains(mockRole));

        verify(userRepository).save(mockUser);
        verify(verificationService).verificationCode(mockUser.getUsername());
    }

    @Test
    void testCreateUser_InvalidEmail() {
        // Thử tạo người dùng với email không hợp lệ
        userCreateRequest.setUsername("invalidEmail");

        // Kiểm tra xem ngoại lệ có được ném ra khi email không hợp lệ
        AppException exception = assertThrows(AppException.class, () -> userService.createUser(userCreateRequest));
        assertEquals("INVALID_EMAIL_FORMAT", exception.getErrorCode());
    }

    @Test
    void testCreateUser_EmailWithoutDepartment() {
        // Thử tạo người dùng mà không có department hợp lệ
        userCreateRequest.setUsername("99unknown@domain.com");  // Email không có mã phòng ban hợp lệ

        when(departmentRepository.findByDepartmentCodeId(anyLong())).thenReturn(java.util.Optional.empty());  // Mock không tìm thấy phòng ban

        // Kiểm tra xem có ném ngoại lệ không tìm thấy department không
        AppException exception = assertThrows(AppException.class, () -> userService.createUser(userCreateRequest));
        assertEquals("DEPARTMENT_NOT_FOUND", exception.getErrorCode());
    }

    @Test
    void testCreateUser_DuplicateUser() {
        // Thử tạo người dùng với username đã tồn tại
        when(userRepository.save(mockUser)).thenThrow();

        // Kiểm tra xem có ném ngoại lệ Duplicate User không
        AppException exception = assertThrows(AppException.class, () -> userService.createUser(userCreateRequest));
        assertEquals("DUPLICATE_USER", exception.getErrorCode());
    }
}
