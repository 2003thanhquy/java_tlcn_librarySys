package com.spkt.librasys.service;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.request.displayzone.DisplayZoneRequest;
import com.spkt.librasys.dto.response.displayzone.DisplayZoneResponse;
import org.springframework.data.domain.Pageable;

/**
 * Interface cung cấp các phương thức để quản lý các khu vực hiển thị trong hệ thống.
 */
public interface DisplayZoneService {

    /**
     * Tạo mới một khu vực hiển thị.
     *
     * @param request Thông tin về khu vực hiển thị cần tạo.
     * @return Thông tin của khu vực hiển thị vừa được tạo.
     */
    DisplayZoneResponse createDisplayZone(DisplayZoneRequest request);

    /**
     * Cập nhật thông tin một khu vực hiển thị.
     *
     * @param id ID của khu vực hiển thị cần cập nhật.
     * @param request Thông tin mới của khu vực hiển thị.
     * @return Thông tin khu vực hiển thị đã được cập nhật.
     */
    DisplayZoneResponse updateDisplayZone(Long id, DisplayZoneRequest request);

    /**
     * Lấy thông tin một khu vực hiển thị theo ID.
     *
     * @param id ID của khu vực hiển thị cần lấy thông tin.
     * @return Thông tin khu vực hiển thị.
     */
    DisplayZoneResponse getDisplayZoneById(Long id);

    /**
     * Lấy danh sách tất cả các khu vực hiển thị, hỗ trợ phân trang.
     *
     * @param pageable Thông tin phân trang.
     * @return Danh sách các khu vực hiển thị trong một trang.
     */
    PageDTO<DisplayZoneResponse> getAllDisplayZones(Pageable pageable);

    /**
     * Xóa khu vực hiển thị theo ID.
     *
     * @param id ID của khu vực hiển thị cần xóa.
     */
    void deleteDisplayZone(Long id);
}
