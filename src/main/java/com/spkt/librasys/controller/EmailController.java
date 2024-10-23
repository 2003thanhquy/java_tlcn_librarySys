package com.spkt.librasys.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/emails")
public class EmailController {

//    EmailService emailService;
//
//    // Gửi email thông báo cho người dùng
//    @PostMapping("/send-notification")
//    public ApiResponse<EmailResponse> sendNotificationEmail(@RequestBody EmailSendRequest request) {
//        EmailResponse response = emailService.sendNotificationEmail(request);
//        return ApiResponse.<EmailResponse>builder()
//                .message("Notification email sent successfully")
//                .result(response)
//                .build();
//    }
//
//    // Gửi email nhắc nhở cho người dùng
//    @PostMapping("/send-reminder")
//    public ApiResponse<EmailResponse> sendReminderEmail(@RequestBody EmailSendRequest request) {
//        EmailResponse response = emailService.sendReminderEmail(request);
//        return ApiResponse.<EmailResponse>builder()
//                .message("Reminder email sent successfully")
//                .result(response)
//                .build();
//    }
}