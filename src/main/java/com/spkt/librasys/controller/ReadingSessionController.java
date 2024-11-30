package com.spkt.librasys.controller;

import com.spkt.librasys.dto.request.readingSession.ReadingSessionRequest;
import com.spkt.librasys.dto.response.ApiResponse;
import com.spkt.librasys.dto.response.readingSession.ReadingSessionResponse;
import com.spkt.librasys.service.ReadingSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Controller để xử lý các yêu cầu liên quan đến phiên đọc tài liệu của người dùng.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reading-sessions")
public class ReadingSessionController {

    private final ReadingSessionService readingSessionService;  // Service để xử lý logic nghiệp vụ

    /**
     * Bắt đầu một phiên đọc mới cho người dùng.
     *
     * @param request Thông tin yêu cầu bắt đầu phiên đọc, bao gồm userId và documentId.
     * @return Phản hồi thông tin phiên đọc đã được tạo.
     */
    @PostMapping("/start")
    public ApiResponse<ReadingSessionResponse> startReadingSession(@RequestBody ReadingSessionRequest request) {
        ReadingSessionResponse sessionResponse = readingSessionService.startReadingSession(request.getUserId(), request.getDocumentId());
        return ApiResponse.<ReadingSessionResponse>builder()
                .result(sessionResponse)
                .message("Bắt đầu phiên đọc thành công")
                .build();
    }

    /**
     * Cập nhật tiến trình đọc của người dùng (trang hiện tại).
     *
     * @param sessionId ID của phiên đọc.
     * @param currentPage Trang hiện tại mà người dùng đang đọc.
     * @return Phản hồi thông tin phiên đọc đã được cập nhật.
     */
    @PutMapping("/{sessionId}")
    public ApiResponse<ReadingSessionResponse> updateReadingSessionProgress(
            @PathVariable Long sessionId, @RequestParam int currentPage) {
        ReadingSessionResponse sessionResponse = readingSessionService.updateReadingProgress(sessionId, currentPage);
        return ApiResponse.<ReadingSessionResponse>builder()
                .result(sessionResponse)
                .message("Cập nhật tiến trình đọc thành công")
                .build();
    }

    /**
     * Xóa một phiên đọc khi người dùng không còn đọc tài liệu nữa.
     *
     * @param sessionId ID của phiên đọc cần xóa.
     * @return Phản hồi về việc xóa phiên đọc thành công.
     */
    @DeleteMapping("/{sessionId}")
    public ApiResponse<Void> deleteReadingSession(@PathVariable Long sessionId) {
        readingSessionService.removeReadingSession(sessionId);
        return ApiResponse.<Void>builder()
                .message("Phiên đọc đã được xóa thành công")
                .build();
    }
}
