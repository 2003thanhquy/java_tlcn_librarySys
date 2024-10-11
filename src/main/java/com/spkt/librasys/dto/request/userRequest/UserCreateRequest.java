package com.spkt.librasys.dto.request.userRequest;
import java.time.LocalDate;

import jakarta.validation.constraints.Size;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreateRequest {
    @Size(min = 4, message = "USERNAME_INVALID")
    String username;

    @Size(min = 6, message = "INVALID_PASSWORD")
    String password;

    String firstName;
    String lastName;

//    @DobConstraint(min = 10, message = "INVALID_DOB")
    LocalDate dob;
}