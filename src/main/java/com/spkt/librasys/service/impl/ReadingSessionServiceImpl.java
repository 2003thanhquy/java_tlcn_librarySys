package com.spkt.librasys.service.impl;

import com.spkt.librasys.dto.request.readingSession.ReadingSessionRequest;
import com.spkt.librasys.dto.response.readingSession.ReadingSessionResponse;
import com.spkt.librasys.entity.Document;
import com.spkt.librasys.entity.ReadingSession;
import com.spkt.librasys.entity.User;
import com.spkt.librasys.exception.AppException;
import com.spkt.librasys.exception.ErrorCode;
import com.spkt.librasys.mapper.ReadingSessionMapper;
import com.spkt.librasys.repository.DocumentRepository;
import com.spkt.librasys.repository.ReadingSessionRepository;
import com.spkt.librasys.repository.access.UserRepository;
import com.spkt.librasys.service.ReadingSessionService;
import com.spkt.librasys.service.SecurityContextService;
import org.springdoc.core.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ReadingSessionServiceImpl implements ReadingSessionService {

    @Autowired
    private ReadingSessionRepository readingSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private ReadingSessionMapper readingSessionMapper;  // Inject mapper
    @Autowired
    private SecurityContextService securityContextService;

    /**
     * Bắt đầu phiên đọc mới cho người dùng.
     *
     * @param request chua documentId ID của tài liệu
     * @return Thông tin phiên đọc mới được tạo, dưới dạng DTO {@link ReadingSessionResponse}
     */
    @Override
    public ReadingSessionResponse startReadingSession(ReadingSessionRequest request) {
        User user = securityContextService.getCurrentUser();
        if(user == null) {
            return ReadingSessionResponse.builder()
                    .currentPage(1)
                    .build();
        }
        Document document = documentRepository.findById(request.getDocumentId()).orElseThrow(() ->
                new AppException(ErrorCode.DOCUMENT_NOT_FOUND));

        // Kiểm tra nếu phiên đọc đã tồn tại, nếu có thì không tạo mới
        Optional<ReadingSession> existingSession = readingSessionRepository.findByUserAndDocument(user, document);
        if (existingSession.isPresent()) {
            return readingSessionMapper.toResponse(existingSession.get()); // Trả về phiên đọc hiện tại
        }

        // Tạo mới phiên đọc
        ReadingSession readingSession = new ReadingSession();
        readingSession.setUser(user);
        readingSession.setDocument(document);
        readingSession.setCurrentPage(1); // Mặc định bắt đầu từ trang đầu tiên
        ReadingSession savedSession = readingSessionRepository.save(readingSession);

        // Trả về thông tin phiên đọc dưới dạng DTO
        return readingSessionMapper.toResponse(savedSession);
    }

    /**
     * Cập nhật tiến trình đọc của người dùng.
     *
     * @param sessionId ID của phiên đọc cần cập nhật
     * @param currentPage Số trang hiện tại mà người dùng đang đọc
     * @return Thông tin phiên đọc đã được cập nhật, dưới dạng DTO {@link ReadingSessionResponse}
     */
    @Override
    public ReadingSessionResponse updateReadingProgress(Long sessionId, int currentPage) {
        ReadingSession readingSession = readingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Reading session not found"));
        readingSession.setCurrentPage(currentPage);
        readingSession.setLastReadAt(LocalDateTime.now());
        ReadingSession updatedSession = readingSessionRepository.save(readingSession);

        // Trả về thông tin phiên đọc dưới dạng DTO
        return readingSessionMapper.toResponse(updatedSession);
    }

    /**
     * Xóa phiên đọc khi người dùng không còn đọc tài liệu nữa.
     *
     * @param sessionId ID của phiên đọc cần xóa
     */
    @Override
    public void removeReadingSession(Long sessionId) {
        readingSessionRepository.deleteById(sessionId);
    }
}

