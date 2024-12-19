package com.spkt.librasys.feature.auth;

import com.spkt.librasys.dto.request.ResetPasswordRequest;
import com.spkt.librasys.entity.User;
import com.spkt.librasys.entity.VerificationToken;
import com.spkt.librasys.repository.VerificationTokenRepository;
import com.spkt.librasys.repository.access.UserRepository;
import com.spkt.librasys.service.EmailService;
import com.spkt.librasys.service.impl.AuthenticationServiceImpl;
import com.spkt.librasys.service.impl.VerificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class ResetPasswordTest {

    @InjectMocks
    private VerificationServiceImpl verificationService;  // Thay bằng tên lớp của bạn

    @Mock
    private UserRepository userRepository;

    @Mock
    private VerificationTokenRepository verificationTokenRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;
    private UUID uuid;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        uuid = mock(UUID.class);
    }

    @Test
    public void testRequestPasswordReset_UserNotFound() {
        String email = "test@example.com";

        // Giả lập trường hợp không tìm thấy người dùng
        when(userRepository.findByUsername(email)).thenReturn(Optional.empty());

        // Gọi phương thức
        verificationService.requestPasswordReset(email);

        // Xác nhận không gọi emailService.sendEmailAsync vì không có người dùng
        verify(emailService, never()).sendEmailAsync(any(), any(), any());
    }

    @Test
    public void testRequestPasswordReset_Success() {
        String email = "test@example.com";
        User user = new User();
        user.setUsername(email);
        String resetToken = "dummy-token";
        mockStatic(UUID.class);
        when(UUID.randomUUID()).thenReturn(uuid);
        when(uuid.toString()).thenReturn(resetToken);
        when(userRepository.findByUsername(email)).thenReturn(Optional.of(user));
        // Gọi phương thức requestPasswordReset
        verificationService.requestPasswordReset(email);

        // Xác nhận gọi emailService.sendEmailAsync với email, chủ đề và nội dung phù hợp
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailService).sendEmailAsync(eq(email), subjectCaptor.capture(), bodyCaptor.capture());

        // Kiểm tra nội dung của email
        assertTrue(subjectCaptor.getValue().contains("Yêu cầu đặt lại mật khẩu"));
        assertTrue(bodyCaptor.getValue().contains("Đây là liên kết để đặt lại mật khẩu của bạn"));
    }

    @Test
    public void testResetPassword_TokenNotFound() {
        String token = "invalid-token";
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken(token);
        request.setNewPassword("newpassword");

        // Giả lập không tìm thấy token
        when(verificationTokenRepository.findByToken(token)).thenReturn(null);

        // Gọi phương thức
        boolean result = verificationService.resetPassword(request);

        // Kiểm tra kết quả
        assertFalse(result);
    }

    @Test
    public void testResetPassword_TokenExpired() {
        String token = "invalid-token";
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken(token);
        request.setNewPassword("newpassword");

        VerificationToken expiredToken = new VerificationToken();
        expiredToken.setToken(token);
        expiredToken.setExpiryDate(LocalDateTime.now().minusMinutes(1));  // Token hết hạn

        // Giả lập token hết hạn
        when(verificationTokenRepository.findByToken(token)).thenReturn(expiredToken);

        // Gọi phương thức
        boolean result = verificationService.resetPassword(request);

        // Kiểm tra kết quả
        assertFalse(result);
    }

    @Test
    public void testResetPassword_Success() {
        String token = "valid-token";
        String email = "test@example.com";
        String newPassword = "newPassword123";
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken(token);
        request.setNewPassword(newPassword);

        VerificationToken validToken = new VerificationToken();
        validToken.setToken(token);
        validToken.setEmail(email);
        validToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));  // Token hợp lệ

        User user = new User();
        user.setUsername(email);

        // Giả lập tìm thấy token và người dùng
        when(verificationTokenRepository.findByToken(token)).thenReturn(validToken);
        when(userRepository.findByUsername(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedPassword");

        // Gọi phương thức
        boolean result = verificationService.resetPassword(request);

        // Kiểm tra kết quả
        assertTrue(result);
        verify(userRepository).save(user);  // Kiểm tra rằng người dùng đã được lưu
    }
}
