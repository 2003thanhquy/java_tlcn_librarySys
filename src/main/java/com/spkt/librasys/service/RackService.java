package com.spkt.librasys.service;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.request.rack.RackRequest;
import com.spkt.librasys.dto.response.rack.RackResponse;
import org.springframework.data.domain.Pageable;

/**
 * Giao diện RackService định nghĩa các hành vi liên quan đến kệ sách (Rack).
 */
public interface RackService {

    /**
     * Tạo một kệ sách mới.
     *
     * @param request Thông tin yêu cầu tạo kệ sách mới.
     * @return RackResponse chứa thông tin kệ sách vừa được tạo.
     */
    RackResponse createRack(RackRequest request);

    /**
     * Cập nhật thông tin của kệ sách.
     *
     * @param id      ID của kệ sách cần cập nhật.
     * @param request Thông tin cập nhật cho kệ sách.
     * @return RackResponse chứa thông tin kệ sách sau khi cập nhật.
     */
    RackResponse updateRack(Long id, RackRequest request);

    /**
     * Lấy thông tin của một kệ sách theo ID.
     *
     * @param id ID của kệ sách cần lấy.
     * @return RackResponse chứa thông tin của kệ sách.
     */
    RackResponse getRackById(Long id);

    /**
     * Lấy danh sách các kệ sách với phân trang.
     *
     * @param pageable Thông tin phân trang.
     * @return PageDTO chứa danh sách kệ sách với phân trang.
     */
    PageDTO<RackResponse> getAllRacks(Pageable pageable);

    /**
     * Xóa một kệ sách theo ID.
     *
     * @param id ID của kệ sách cần xóa.
     */
    void deleteRack(Long id);
}
