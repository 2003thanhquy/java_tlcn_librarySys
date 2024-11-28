package com.spkt.librasys.service;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.request.shelf.ShelfRequest;
import com.spkt.librasys.dto.response.shelf.ShelfResponse;
import org.springframework.data.domain.Pageable;

/**
 * Giao diện ShelfService định nghĩa các hành vi liên quan đến các kệ sách trong hệ thống thư viện.
 */
public interface ShelfService {

    /**
     * Tạo mới một kệ sách.
     *
     * @param request Dữ liệu cần thiết để tạo một kệ sách mới.
     * @return ShelfResponse chứa thông tin của kệ sách vừa tạo.
     */
    ShelfResponse createShelf(ShelfRequest request);

    /**
     * Cập nhật thông tin của một kệ sách.
     *
     * @param id      ID của kệ sách cần cập nhật.
     * @param request Thông tin mới cần cập nhật cho kệ sách.
     * @return ShelfResponse chứa thông tin của kệ sách sau khi cập nhật.
     */
    ShelfResponse updateShelf(Long id, ShelfRequest request);

    /**
     * Lấy thông tin của kệ sách theo ID.
     *
     * @param id ID của kệ sách.
     * @return ShelfResponse chứa thông tin chi tiết của kệ sách.
     */
    ShelfResponse getShelfById(Long id);

    /**
     * Lấy danh sách tất cả các kệ sách với phân trang.
     *
     * @param pageable Thông tin phân trang.
     * @return PageDTO chứa danh sách kệ sách và thông tin phân trang.
     */
    PageDTO<ShelfResponse> getAllShelves(Pageable pageable);

    /**
     * Xóa kệ sách theo ID.
     *
     * @param id ID của kệ sách cần xóa.
     */
    void deleteShelf(Long id);
}
