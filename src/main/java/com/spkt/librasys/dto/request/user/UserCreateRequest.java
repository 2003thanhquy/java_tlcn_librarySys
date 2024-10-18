package com.spkt.librasys.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreateRequest {

    @NotBlank(message = "USERNAME_CANNOT_BE_BLANK")
    @Email(message = "USERNAME_INVALID_EMAIL_FORMAT") // Kiểm tra định dạng email
    String username;

    @NotBlank(message = "PASSWORD_CANNOT_BE_BLANK")
    @Size(min = 6, message = "INVALID_PASSWORD")
    String password;

    @NotBlank(message = "FIRST_NAME_CANNOT_BE_BLANK")
    String firstName;

    @NotBlank(message = "LAST_NAME_CANNOT_BE_BLANK")
    String lastName;

    LocalDate dob;

    @NotBlank(message = "PHONE_NUMBER_CANNOT_BE_BLANK")
    String phoneNumber;

    @NotBlank(message = "ADDRESS_CANNOT_BE_BLANK")
    String address;
}
