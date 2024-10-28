package com.spkt.librasys.mapper;

import com.spkt.librasys.dto.request.review.ReviewCreateRequest;
import com.spkt.librasys.dto.request.review.ReviewUpdateRequest;
import com.spkt.librasys.dto.response.review.ReviewResponse;
import com.spkt.librasys.entity.Review;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "document", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    Review toReview(ReviewCreateRequest request);

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "document.documentId", target = "documentId")
    ReviewResponse toReviewResponse(Review review);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "document", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateReview(@MappingTarget Review review, ReviewUpdateRequest request);
}
