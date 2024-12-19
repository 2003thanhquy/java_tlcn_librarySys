package com.spkt.librasys.service;

import com.nimbusds.jose.JOSEException;
import com.spkt.librasys.dto.request.AuthenticationRequest;
import com.spkt.librasys.dto.request.IntrospectRequest;
import com.spkt.librasys.dto.request.LogoutRequest;
import com.spkt.librasys.dto.request.RefreshRequest;
import com.spkt.librasys.dto.response.AuthenticationResponse;
import com.spkt.librasys.entity.InvalidatedToken;
import com.spkt.librasys.entity.User;
import com.spkt.librasys.exception.AppException;
import com.spkt.librasys.exception.ErrorCode;
import com.spkt.librasys.repository.access.InvalidatedTokenRepository;
import com.spkt.librasys.repository.access.UserRepository;
import com.spkt.librasys.service.impl.AuthenticationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthenticationServiceImplTest {

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private InvalidatedTokenRepository invalidatedTokenRepository;

    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);
        passwordEncoder = new BCryptPasswordEncoder(10);

        // Sử dụng Reflection để đặt giá trị cho SIGNER_KEY
        Field signerKeyField = AuthenticationServiceImpl.class.getDeclaredField("SIGNER_KEY");
        signerKeyField.setAccessible(true); // Cho phép truy cập vào biến private/protected
        signerKeyField.set(authenticationService, "0fCUCfplKd4o5fNUOBAkk1waUF7wbSHrPM5+MABZtLb13nzOw1yLekTVI2ChTbRs"); // Đặt giá trị cho SIGNER_KEY
    }

    @Test
    public void testLoginSuccess() {
        // Giả lập yêu cầu đăng nhập
        AuthenticationRequest request = new AuthenticationRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        // Giả lập đối tượng User từ repository
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setIsActive(User.Status.ACTIVE);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Thực hiện kiểm thử login
        AuthenticationResponse response = authenticationService.login(request);
        System.out.printf(response.getToken());
        // Xác nhận phản hồi không null và chứa token
        assertNotNull(response);
        assertNotNull(response.getToken());
    }

    @Test
    public void testLoginUserNotFound() {
        // Giả lập yêu cầu đăng nhập
        AuthenticationRequest request = new AuthenticationRequest();
        request.setUsername("unknownuser");
        request.setPassword("password123");

        when(userRepository.findByUsername("unknownuser")).thenReturn(Optional.empty());

        // Thực hiện kiểm thử và mong đợi AppException với mã lỗi USER_NOT_FOUND
        AppException exception = assertThrows(AppException.class, () -> {
            authenticationService.login(request);
        });
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    public void testLogout() throws ParseException, JOSEException {
        // Giả lập yêu cầu logout
        LogoutRequest request = new LogoutRequest();
        request.setToken("dummyToken");

        // Giả lập verifyToken trả về SignedJWT
        doNothing().when(invalidatedTokenRepository).save(any(InvalidatedToken.class));

        // Thực hiện kiểm thử logout
        assertDoesNotThrow(() -> authenticationService.logout(request));
    }

    @Test
    public void testRefreshTokenExpired() throws ParseException, JOSEException {
        // Giả lập yêu cầu làm mới token
        RefreshRequest request = new RefreshRequest();
        request.setToken("expiredToken");

        // Giả lập đối tượng SignedJWT đã hết hạn
        User user = new User();
        user.setUsername("testuser");

        // Mô phỏng verifyToken trả về token đã hết hạn
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        // Thực hiện kiểm thử và mong đợi AppException với mã lỗi TOKEN_EXPIRED
        AppException exception = assertThrows(AppException.class, () -> {
            authenticationService.refresh(request);
        });
        assertEquals(ErrorCode.TOKEN_EXPIRED, exception.getErrorCode());
    }

    @Test
    public void testIntrospectValidToken() {
        // Giả lập introspect request
        var token = IntrospectRequest.builder().token("newtoken").build();

        // Mô phỏng verifyToken trả về hợp lệ
        doNothing().when(invalidatedTokenRepository).existsById(anyString());

        // Thực hiện introspect
        var response = authenticationService.introspect(token);

        // Xác nhận token là hợp lệ
        assertTrue(response.isValid());
    }
}
