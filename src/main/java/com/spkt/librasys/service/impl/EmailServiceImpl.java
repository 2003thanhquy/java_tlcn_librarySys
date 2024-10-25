package com.spkt.librasys.service.impl;

import com.github.javafaker.Bool;
import com.spkt.librasys.entity.Email;
import com.spkt.librasys.repository.EmailRepository;
import com.spkt.librasys.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
public class EmailServiceImpl implements EmailService {

    private static String EMAIL_HOST = "quy2003@wuy.id.vn";
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    EmailRepository emailRepository;

    @Override
    @Async
    public CompletableFuture<Boolean> sendTextEmail(Email email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email.getToEmail());
        message.setSubject(email.getSubject());
        message.setText(email.getBody());
        message.setFrom(EMAIL_HOST);
        try{
            mailSender.send(message);
            System.out.println("SendTextEmail sent thanh cong ");
        }catch (Exception e) {
           // throw new RuntimeException(e);
            return CompletableFuture.completedFuture(false);
        }
        return CompletableFuture.completedFuture(true);

    }

    @Override
    public String sendHtmlEmail(Email email) {
        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(EMAIL_HOST);
            helper.setTo(email.getToEmail());
            helper.setSubject(email.getSubject());
            helper.setText(email.getBody(), true);
            mailSender.send(message);
            System.out.println("Email sent successfully");
            return "Email sent successfully";
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public String sendAttachmentsEmail(Email email) {
        return "";
    }
    @Async
    public CompletableFuture<Boolean> sendVerificationCodeAsync(String toEmail, String verificationCode) {
        // Tạo email để lưu vào database
        Email email = Email.builder()
                .toEmail(toEmail)
                .subject("Xác minh tài khoản của bạn")
                .body("Chào bạn,\n\nĐây là mã xác minh của bạn: " + verificationCode +
                        "\nMã này sẽ hết hạn sau 10 phút.\n\nTrân trọng,\nĐội ngũ hỗ trợ.")
                .createdAt(LocalDateTime.now())
                .build();

        return CompletableFuture.supplyAsync(() -> {
            try {
                sendHtmlEmail(email); // Gửi email qua service
                email.setStatus("SUCCESS"); // Đánh dấu trạng thái gửi thành công
            } catch (Exception e) {
                email.setStatus("FAILED"); // Đánh dấu trạng thái thất bại
            }
            // Lưu email vào database
            emailRepository.save(email);
            return email.getStatus().equals("SUCCESS");
        });
    }
}
