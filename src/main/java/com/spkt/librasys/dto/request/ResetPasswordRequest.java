package com.spkt.librasys.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
    @NotBlank(message = "TOKEN_CANNOT_BE_BLANK")
    private String token;  // Mã reset mật khẩu
    @NotBlank(message = "PASSWORD_CANNOT_BE_BLANK")
    @Size(min = 6, message = "INVALID_PASSWORD")
    private String newPassword;  // Mật khẩu mới

    // Getters và Setters
}
