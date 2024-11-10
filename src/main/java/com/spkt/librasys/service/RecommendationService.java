package com.spkt.librasys.service;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.response.document.DocumentResponse;
import com.spkt.librasys.dto.response.programclass.ProgramClassResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RecommendationService {
    /**
     * Lấy danh sách Document được đề xuất cho người dùng hiện tại với phân trang.
     *
     * @param pageable Thông tin phân trang.
     * @return Một đối tượng PageDTO chứa danh sách DocumentResponse.
     */
    PageDTO<DocumentResponse> getRecommendedDocumentsForCurrentUser(Pageable pageable);
}
