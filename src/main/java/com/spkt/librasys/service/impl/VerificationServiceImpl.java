package com.spkt.librasys.service.impl;

import com.spkt.librasys.dto.request.ResetPasswordRequest;
import com.spkt.librasys.dto.request.VerificationRequest;
import com.spkt.librasys.entity.Email;
import com.spkt.librasys.entity.User;
import com.spkt.librasys.entity.VerificationToken;
import com.spkt.librasys.repository.VerificationTokenRepository;
import com.spkt.librasys.repository.access.UserRepository;
import com.spkt.librasys.service.EmailService;
import com.spkt.librasys.service.VerificationService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VerificationServiceImpl  implements VerificationService {
    VerificationTokenRepository verificationTokenRepository;
    UserRepository userRepository;
    EmailService emailService;
    PasswordEncoder passwordEncoder;
    @Value("${my-config.base-url}")
    @NonFinal
    private String baseUrl;
    @Override
    public boolean verifyAccount(VerificationRequest request) {
        VerificationToken token = verificationTokenRepository.findByToken(request.getEmail());

        if (token != null && token.getExpiryDate().isAfter(LocalDateTime.now())) {
            Optional<User> userOptional = userRepository.findByUsername(request.getEmail());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.setIsActive(User.Status.ACTIVE);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }
    @Override
    public  void verificationCode(String email){
        // Tạo mã mới và lưu vào database
        String verificationToken = UUID.randomUUID().toString();
        VerificationToken newToken = VerificationToken.builder()
                .email(email)
                .token(verificationToken)
                .expiryDate(LocalDateTime.now().plusMinutes(10))
                .type(VerificationToken.TokenType.VERIFICATION)
                .build();
        verificationTokenRepository.save(newToken);
        // Gửi email xác minh
        String verificationUrl = baseUrl + "/api/v1/auth/verify-email?token=" + verificationToken;
        // Nội dung email cho xác minh tài khoản
        String subject = "Xác minh tài khoản của bạn";
        String body = "Chào bạn,\n\nĐây là mã xác minh của bạn: " + verificationUrl +
                "\nMã này sẽ hết hạn sau 10 phút.\n\nTrân trọng,\nĐội ngũ hỗ trợ.";

        // Gửi email xác minh
        emailService.sendEmailAsync(email, subject, body);
    }

    @Override
    public boolean resendVerificationCode(String email) {
        var userOptional = userRepository.findByUsername(email);
        if (userOptional.isEmpty()) return false;
        User user = userOptional.get();
        if (user.getIsActive() != User.Status.PENDING) {
            return false;  // Không thể gửi lại mã cho user không tồn tại hoặc đã được xác minh
        }
        verificationCode(email);
        return true;
    }
    // Xử lý quên mật khẩu (tạo mã reset mật khẩu)
    @Override
    public void requestPasswordReset(String email) {
        Optional<User> userOptional = userRepository.findByUsername(email);
        if (userOptional.isEmpty()) {
            // Nếu không có người dùng với email này, có thể trả về lỗi hoặc không làm gì
            log.warn("No user found with email: " + email);
            return;
        }

        // Tạo mã reset mật khẩu
        String resetToken = UUID.randomUUID().toString();
        VerificationToken newResetToken = VerificationToken.builder()
                .email(email)
                .token(resetToken)
                .expiryDate(LocalDateTime.now().plusMinutes(15))  // Hạn sử dụng token
                .build();

        verificationTokenRepository.save(newResetToken);

        // Gửi email với link để đặt lại mật khẩu
        String resetPasswordUrl = baseUrl + "/api/v1/auth/reset-password?token=" + resetToken;
        // Nội dung email cho đặt lại mật khẩu
        String subject = "Yêu cầu đặt lại mật khẩu";
        String body = "Chào bạn,\n\nĐây là liên kết để đặt lại mật khẩu của bạn: " + resetPasswordUrl +
                "\nMã này sẽ hết hạn sau 15 phút.\n\nTrân trọng,\nĐội ngũ hỗ trợ.";
        // Gửi email reset mật khẩu
        emailService.sendEmailAsync(email, subject, body);

    }

    // Xử lý xác thực mã reset mật khẩu và thay đổi mật khẩu
    @Override
    public boolean resetPassword(ResetPasswordRequest request) {
        VerificationToken resetToken = verificationTokenRepository.findByToken(request.getToken());

        if (resetToken == null || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            // Token không hợp lệ hoặc đã hết hạn
            return false;
        }

        Optional<User> userOptional = userRepository.findByUsername(resetToken.getEmail());
        if (userOptional.isEmpty()) {
            return false;
        }

        User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));  // Cập nhật mật khẩu mới (nên mã hóa mật khẩu trước)
        userRepository.save(user);

        // Xóa token reset mật khẩu sau khi sử dụng
        // verificationTokenRepository.delete(resetToken);
        return true;
    }

}
