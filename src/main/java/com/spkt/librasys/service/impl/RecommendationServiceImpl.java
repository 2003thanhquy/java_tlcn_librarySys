package com.spkt.librasys.service.impl;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.response.document.DocumentResponse;
import com.spkt.librasys.entity.Course;
import com.spkt.librasys.entity.Document;
import com.spkt.librasys.entity.ProgramClass;
import com.spkt.librasys.entity.User;
import com.spkt.librasys.exception.AppException;
import com.spkt.librasys.exception.ErrorCode;
import com.spkt.librasys.mapper.DocumentMapper;
import com.spkt.librasys.repository.DocumentRepository;
import com.spkt.librasys.repository.ProgramClassRepository;
import com.spkt.librasys.repository.access.UserRepository;
import com.spkt.librasys.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final UserRepository userRepository;
    private final ProgramClassRepository programClassRepository;
    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;

    @Override
    public PageDTO<DocumentResponse> getRecommendedDocumentsForCurrentUser(Pageable pageable) {
        // Lấy thông tin người dùng hiện tại từ SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        // Tìm kiếm người dùng dựa trên username
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy người dùng với username: " + currentUsername));

        // Kiểm tra thông tin studentBatch và department của người dùng
        if (user.getStudentBatch() == 0 || user.getDepartment() == null) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Người dùng không có thông tin về studentBatch hoặc department.");
        }

        // Tìm các ProgramClass phù hợp với studentBatch và department của người dùng
        List<ProgramClass> matchingProgramClasses = programClassRepository.findByStudentBatchAndDepartment(user.getStudentBatch(), user.getDepartment());
        if (matchingProgramClasses.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Không tìm thấy ProgramClass phù hợp với thông tin của người dùng.");
        }

        // Lấy tất cả các khóa học từ các ProgramClass phù hợp
        Set<Course> userCourses = matchingProgramClasses.stream()
                .flatMap(programClass -> programClass.getCourses().stream())
                .collect(Collectors.toSet());

        if (userCourses.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Không có khóa học nào phù hợp cho người dùng.");
        }

        // Lấy các tài liệu liên kết với các khóa học của người dùng với phân trang
        Page<Document> recommendedDocumentsPage = documentRepository.findByCoursesIn(userCourses, pageable);

        // Chuyển đổi các thực thể Document thành các DTO DocumentResponse sử dụng mapper
        Page<DocumentResponse> pages = recommendedDocumentsPage.map(documentMapper::toDocumentResponse);

        return new PageDTO<>(pages);
    }
}
