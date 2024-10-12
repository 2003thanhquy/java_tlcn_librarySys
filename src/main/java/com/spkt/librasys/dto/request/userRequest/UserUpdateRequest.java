package com.spkt.librasys.dto.request.userRequest;

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
    String password;
    String firstName;
    String lastName;

//    @DobConstraint(min = 18, message = "INVALID_DOB")
    LocalDate dob;
    String phoneNumber;
    String address;
    LocalDate registrationDate;
    LocalDate expirationDate;

    List<String> roles;

}