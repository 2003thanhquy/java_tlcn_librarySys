package com.spkt.librasys.feature.auth;

import com.spkt.librasys.dto.request.AuthenticationRequest;
import com.spkt.librasys.dto.response.AuthenticationResponse;
import com.spkt.librasys.entity.User;
import com.spkt.librasys.exception.AppException;
import com.spkt.librasys.exception.ErrorCode;
import com.spkt.librasys.repository.access.InvalidatedTokenRepository;
import com.spkt.librasys.repository.access.UserRepository;
import com.spkt.librasys.service.AuthenticationService;
import com.spkt.librasys.service.impl.AuthenticationServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
public class LoginUnitTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private InvalidatedTokenRepository invalidatedTokenRepository;

    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private AuthenticationRequest authenticationRequest;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);
        passwordEncoder = new BCryptPasswordEncoder(10);

        // Sử dụng Reflection để đặt giá trị cho SIGNER_KEY
        Field signerKeyField = AuthenticationServiceImpl.class.getDeclaredField("SIGNER_KEY");
        signerKeyField.setAccessible(true); // Cho phép truy cập vào biến private/protected
        signerKeyField.set(authenticationService, "0fCUCfplKd4o5fNUOBAkk1waUF7wbSHrPM5+MABZtLb13nzOw1yLekTVI2ChTbRs"); // Đặt giá trị cho SIGNER_KEY



        // Khởi tạo đối tượng request giả để test
        authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setUsername("testuser");
        authenticationRequest.setPassword("testpassword");
    }
    //happy pass
    @Test
    void testLogin_Success() {
        // Tạo người dùng giả trong cơ sở dữ liệu
        User mockUser = User.builder()
                .username("testuser")
                .password(passwordEncoder.encode("testpassword"))
                .isActive(User.Status.ACTIVE)
                .build();

        // Mock các phương thức
        when(userRepository.findByUsername(authenticationRequest.getUsername())).thenReturn(Optional.of(mockUser));
        // Gọi phương thức login
        AuthenticationResponse response = authenticationService.login(authenticationRequest);
        // Kiểm tra kết quả
        assertNotNull(response);
        assertNotNull(response.getToken());

    }

    //unhappy pass
    @Test
    void testLogin_UserNotFound() {
        // Mock trả về không tìm thấy người dùng
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // Kiểm tra ngoại lệ khi người dùng không tồn tại
        AppException exception = assertThrows(AppException.class, () -> authenticationService.login(authenticationRequest));
        assertEquals("USER_NOT_FOUND", exception.getErrorCode().name());
    }

    @Test
    void testLogin_InvalidPassword() {
        // Tạo người dùng giả
        User mockUser = new User();
        mockUser.setUsername("testuser");
        mockUser.setPassword(passwordEncoder.encode("testpassword1"));
        mockUser.setIsActive(User.Status.ACTIVE);

        // Mock trả về người dùng
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        // Kiểm tra khi mật khẩu sai
        AppException exception = assertThrows(AppException.class, () -> authenticationService.login(authenticationRequest));
        assertEquals("INVALID_CREDENTIALS", exception.getErrorCode().name());
    }

    @Test
    void testLogin_UserLocked() {
        // Tạo người dùng giả với trạng thái bị khóa
        User mockUser = new User();
        mockUser.setUsername("testuser");
        mockUser.setPassword(new BCryptPasswordEncoder().encode("testpassword"));
        mockUser.setIsActive(User.Status.LOCKED);

        // Mock trả về người dùng
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        // Kiểm tra khi tài khoản bị khóa
        AppException exception = assertThrows(AppException.class, () -> authenticationService.login(authenticationRequest));
        assertEquals("USER_LOCKED", exception.getErrorCode().name());
    }

    @Test
    void testLogin_UserDeactivated() {
        // Tạo người dùng giả với trạng thái bị vô hiệu hóa
        User mockUser = new User();
        mockUser.setUsername("testuser");
        mockUser.setPassword(new BCryptPasswordEncoder().encode("testpassword"));
        mockUser.setIsActive(User.Status.DEACTIVATED);

        // Mock trả về người dùng
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        // Kiểm tra khi tài khoản bị vô hiệu hóa
        AppException exception = assertThrows(AppException.class, () -> authenticationService.login(authenticationRequest));
        assertEquals(ErrorCode.USER_DEACTIVATED.name(), exception.getErrorCode().name());
    }
    @Test
    void testLogin_UserPending() {
        // Tạo người dùng giả với trạng thái bị vô hiệu hóa
        User mockUser = new User();
        mockUser.setUsername("testuser");
        mockUser.setPassword(new BCryptPasswordEncoder().encode("testpassword"));
        mockUser.setIsActive(User.Status.PENDING);

        // Mock trả về người dùng
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        // Kiểm tra khi tài khoản bị vô hiệu hóa
        AppException exception = assertThrows(AppException.class, () -> authenticationService.login(authenticationRequest));
        assertEquals(ErrorCode.USER_PENDING.name(), exception.getErrorCode().name());
    }

}
