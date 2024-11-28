package com.spkt.librasys.service;

import com.spkt.librasys.dto.response.accessHistory.AccessHistoryResponse;
import com.spkt.librasys.entity.AccessHistory;
import com.spkt.librasys.entity.Document;
import com.spkt.librasys.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interface cung cấp các phương thức để xử lý lịch sử truy cập của người dùng vào tài liệu.
 * Các phương thức này cho phép ghi lại, lấy thông tin, tìm kiếm và xóa các bản ghi lịch sử truy cập.
 */
public interface AccessHistoryService {

    /**
     * Ghi lại lịch sử truy cập của người dùng đối với tài liệu.
     *
     * @param user Người dùng thực hiện hành động
     * @param document Tài liệu mà người dùng truy cập
     * @param activity Hành động của người dùng (ví dụ: xem, chỉnh sửa, xóa)
     */
    void recordAccess(User user, Document document, AccessHistory.Activity activity);

    /**
     * Lấy danh sách tất cả các lịch sử truy cập với phân trang.
     *
     * @param pageable Thông tin phân trang
     * @return Trang danh sách các lịch sử truy cập
     */
    Page<AccessHistoryResponse> getAllAccessHistories(Pageable pageable);

    /**
     * Lấy thông tin lịch sử truy cập theo ID.
     *
     * @param id ID của lịch sử truy cập
     * @return Lịch sử truy cập tương ứng
     */
    AccessHistoryResponse getAccessHistoryById(Long id);

    /**
     * Tìm kiếm các bản ghi lịch sử truy cập theo các tiêu chí cụ thể.
     *
     * @param userId ID người dùng
     * @param documentId ID tài liệu
     * @param activity Loại hành động (ví dụ: xem, chỉnh sửa)
     * @param fromDate Ngày bắt đầu tìm kiếm
     * @param toDate Ngày kết thúc tìm kiếm
     * @param pageable Thông tin phân trang
     * @return Trang kết quả tìm kiếm lịch sử truy cập
     */
    Page<AccessHistoryResponse> searchAccessHistories(
            String userId, Long documentId, String activity, String fromDate, String toDate, Pageable pageable);

    /**
     * Xóa một bản ghi lịch sử truy cập theo ID.
     *
     * @param id ID của bản ghi lịch sử truy cập cần xóa
     */
    void deleteAccessHistoryById(Long id);
}
