package com.spkt.librasys.service;

import com.spkt.librasys.entity.Email;

import java.util.concurrent.CompletableFuture;

public interface EmailService {
    CompletableFuture<Boolean> sendTextEmail(Email email);
    String sendHtmlEmail(Email email);
    String sendAttachmentsEmail(Email email);

    CompletableFuture<Boolean> sendEmailAsync(String toEmail, String subject, String body);
}
