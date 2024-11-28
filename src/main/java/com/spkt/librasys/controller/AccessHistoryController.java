package com.spkt.librasys.controller;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.request.accessHistory.AccessHistorySearchDTO;
import com.spkt.librasys.dto.response.accessHistory.AccessHistoryResponse;
import com.spkt.librasys.dto.response.ApiResponse;
import com.spkt.librasys.service.AccessHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/**
 * Controller để xử lý các yêu cầu liên quan đến lịch sử truy cập.
 * Cung cấp các phương thức để lấy, tìm kiếm, và xóa lịch sử truy cập.
 */
@RestController
@RequestMapping("/api/v1/access-histories")
@RequiredArgsConstructor
public class AccessHistoryController {

    private final AccessHistoryService accessHistoryService;

    /**
     * Lấy tất cả lịch sử truy cập có phân trang.
     *
     * @param pageable Tham số phân trang để chỉ định số trang và kích thước trang.
     * @return Một đối tượng ApiResponse chứa danh sách lịch sử truy cập phân trang.
     */
    @GetMapping
    public ApiResponse<PageDTO<AccessHistoryResponse>> getAllAccessHistories(Pageable pageable) {
        // Gọi service để lấy danh sách lịch sử truy cập với phân trang
        Page<AccessHistoryResponse> responseList = accessHistoryService.getAllAccessHistories(pageable);
        PageDTO<AccessHistoryResponse> pageDTO = new PageDTO<>(responseList);
        // Trả về kết quả thành công với thông tin lịch sử truy cập
        return ApiResponse.<PageDTO<AccessHistoryResponse>>builder()
                .message("Access histories retrieved successfully")
                .result(pageDTO)
                .build();
    }

    /**
     * Lấy thông tin lịch sử truy cập theo ID.
     *
     * @param id ID của lịch sử truy cập cần lấy.
     * @return Một đối tượng ApiResponse chứa thông tin chi tiết lịch sử truy cập.
     */
    @GetMapping("/{id}")
    public ApiResponse<AccessHistoryResponse> getAccessHistoryById(@PathVariable Long id) {
        // Gọi service để lấy lịch sử truy cập theo ID
        AccessHistoryResponse response = accessHistoryService.getAccessHistoryById(id);
        // Trả về kết quả thành công với thông tin lịch sử truy cập
        return ApiResponse.<AccessHistoryResponse>builder()
                .message("Access history retrieved successfully")
                .result(response)
                .build();
    }

    /**
     * Tìm kiếm nâng cao lịch sử truy cập (có phân trang).
     *
     * @param searchDTO DTO chứa các tham số tìm kiếm.
     * @param pageable Tham số phân trang.
     * @return Một đối tượng ApiResponse chứa danh sách lịch sử truy cập tìm kiếm được.
     */
    @GetMapping("/search")
    public ApiResponse<PageDTO<AccessHistoryResponse>> searchAccessHistories(
            AccessHistorySearchDTO searchDTO, Pageable pageable) {
        // Gọi service để thực hiện tìm kiếm lịch sử truy cập theo các tham số từ DTO
        Page<AccessHistoryResponse> responseList = accessHistoryService.searchAccessHistories(
                searchDTO.getUserId(),
                searchDTO.getDocumentId(),
                searchDTO.getActivity(),
                searchDTO.getFromDate(),
                searchDTO.getToDate(),
                pageable);

        PageDTO<AccessHistoryResponse> pageDTO = new PageDTO<>(responseList);
        // Trả về kết quả tìm kiếm thành công
        return ApiResponse.<PageDTO<AccessHistoryResponse>>builder()
                .message("Access histories retrieved successfully")
                .result(pageDTO)
                .build();
    }

    /**
     * Xóa lịch sử truy cập theo ID.
     *
     * @param id ID của lịch sử truy cập cần xóa.
     * @return Một đối tượng ApiResponse với thông báo xóa thành công.
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAccessHistory(@PathVariable Long id) {
        // Gọi service để xóa lịch sử truy cập theo ID
        accessHistoryService.deleteAccessHistoryById(id);
        // Trả về kết quả xóa thành công
        return ApiResponse.<Void>builder()
                .message("Access history deleted successfully")
                .build();
    }
}
