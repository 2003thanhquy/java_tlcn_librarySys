package com.spkt.librasys.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {

    // Ràng buộc độ dài mật khẩu nếu cần cập nhật
    @Size(min = 6, message = "INVALID_PASSWORD")
    String password;

    // Ràng buộc thông tin tên không được để trống nếu cần cập nhật
    @NotBlank(message = "FIRST_NAME_CANNOT_BE_BLANK")
    String firstName;

    @NotBlank(message = "LAST_NAME_CANNOT_BE_BLANK")
    String lastName;

    // Ràng buộc về tuổi, ví dụ ngày sinh phải trước ngày hiện tại và phải trên 18 tuổi
    LocalDate dob;

    @NotBlank(message = "PHONE_NUMBER_CANNOT_BE_BLANK")
    String phoneNumber;

    @NotBlank(message = "ADDRESS_CANNOT_BE_BLANK")
    String address;

    // Đảm bảo chỉ cập nhật thông qua logic phù hợp của hệ thống
    LocalDate registrationDate;
    LocalDate expirationDate;

    // Danh sách vai trò người dùng
    List<String> roles;

}
