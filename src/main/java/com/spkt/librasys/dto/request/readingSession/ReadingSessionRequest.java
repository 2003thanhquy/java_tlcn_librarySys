package com.spkt.librasys.dto.request.readingSession;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class ReadingSessionRequest {
    private String userId;
    private Long documentId;

    // Getters and Setters
}