package com.spkt.librasys.service;

import com.spkt.librasys.dto.request.readingSession.ReadingSessionRequest;
import com.spkt.librasys.dto.response.readingSession.ReadingSessionResponse;
import com.spkt.librasys.entity.ReadingSession;

/**
 * Dịch vụ quản lý tiến trình đọc của người dùng.
 */
public interface ReadingSessionService {

    /**
     * Bắt đầu phiên đọc mới cho người dùng.
     *
     * @param request ID của tài liệu
     * @return Thông tin phiên đọc mới được tạo, dưới dạng DTO {@link ReadingSessionResponse}
     */
    ReadingSessionResponse startReadingSession(ReadingSessionRequest request);

    /**
     * Cập nhật tiến trình đọc của người dùng.
     *
     * @param sessionId ID của phiên đọc cần cập nhật
     * @param currentPage Số trang hiện tại mà người dùng đang đọc
     * @return Thông tin phiên đọc đã được cập nhật, dưới dạng DTO {@link ReadingSessionResponse}
     */
    ReadingSessionResponse updateReadingProgress(Long sessionId, int currentPage);

    /**
     * Xóa phiên đọc khi người dùng không còn đọc tài liệu nữa.
     *
     * @param sessionId ID của phiên đọc cần xóa
     */
    void removeReadingSession(Long sessionId);
}
