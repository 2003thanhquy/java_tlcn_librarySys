package com.spkt.librasys.dto.request.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewCreateRequest {

    @Min(1)
    @Max(5)
    int rating;

    @NotBlank(message = "Comment cannot be blank")
    String comment;

    @NotBlank(message = "Document ID cannot be blank")
    String documentId;
}
