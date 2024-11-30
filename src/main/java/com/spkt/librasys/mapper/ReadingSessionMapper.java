package com.spkt.librasys.mapper;

import com.spkt.librasys.dto.response.readingSession.ReadingSessionResponse;
import com.spkt.librasys.entity.ReadingSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReadingSessionMapper {

    /**
     * Chuyển đổi từ ReadingSession entity sang ReadingSessionResponse DTO.
     *
     * @param readingSession đối tượng ReadingSession cần chuyển đổi
     * @return ReadingSessionResponse DTO
     */
    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "document.documentId", target = "documentId")
    ReadingSessionResponse toResponse(ReadingSession readingSession);
}
