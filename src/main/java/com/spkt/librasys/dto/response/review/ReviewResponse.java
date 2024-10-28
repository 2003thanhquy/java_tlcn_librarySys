package com.spkt.librasys.dto.response.review;

import com.spkt.librasys.entity.Review;
import com.spkt.librasys.entity.Review.Status;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewResponse {

    Long reviewId;
    String userId;
    Long documentId;
    int rating;
    String comment;
    LocalDateTime createdAt;
    Status status;
}
