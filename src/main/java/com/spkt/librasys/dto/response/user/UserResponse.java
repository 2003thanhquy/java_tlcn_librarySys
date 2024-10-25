package com.spkt.librasys.dto.response.user;

import java.time.LocalDate;
import java.util.List;

import com.spkt.librasys.entity.Role;
import com.spkt.librasys.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String userId;
    String username;
    String firstName;
    String lastName;
    LocalDate dob;
    String phoneNumber;
    String address;
    LocalDate registrationDate;
    LocalDate expirationDate;
    int currentBorrowedCount;
    int maxBorrowLimit;
    User.Status is_active;
    List<Role> roles;
}
