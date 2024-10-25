package com.spkt.librasys.service.impl;

import com.spkt.librasys.dto.request.VerificationRequest;
import com.spkt.librasys.entity.Email;
import com.spkt.librasys.entity.User;
import com.spkt.librasys.entity.VerificationToken;
import com.spkt.librasys.repository.VerificationTokenRepository;
import com.spkt.librasys.repository.access.UserRepository;
import com.spkt.librasys.service.EmailService;
import com.spkt.librasys.service.VerificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VerificationServiceImpl  implements VerificationService {
    VerificationTokenRepository verificationTokenRepository;
    UserRepository userRepository;
    EmailService emailService;

    @Override
    public boolean verifyAccount(VerificationRequest request) {
        VerificationToken token = verificationTokenRepository.findByEmailAndToken(request.getEmail(), request.getVerificationCode());

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
        String newVerificationCode = generateVerificationCode();
        VerificationToken newToken = VerificationToken.builder()
                .email(email)
                .token(newVerificationCode)
                .expiryDate(LocalDateTime.now().plusMinutes(10))
                .build();
        verificationTokenRepository.save(newToken);

        // Gửi lại mã qua email
        emailService.sendVerificationCodeAsync(email, newVerificationCode);
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
    @Override
    public String generateVerificationCode() {
        int length = 6;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder verificationCode = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            verificationCode.append(characters.charAt(random.nextInt(characters.length())));
        }
        return verificationCode.toString();
    }

}
