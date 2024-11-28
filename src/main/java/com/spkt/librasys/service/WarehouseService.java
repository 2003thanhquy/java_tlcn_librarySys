package com.spkt.librasys.service;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.request.warehouse.WarehouseRequest;
import com.spkt.librasys.dto.response.warehouse.WarehouseResponse;
import org.springframework.data.domain.Pageable;

/**
 * Giao diện WarehouseService định nghĩa các hành vi liên quan đến việc quản lý kho lưu trữ.
 * Các phương thức bao gồm tạo mới, cập nhật, lấy thông tin kho, phân trang danh sách kho, và xóa kho.
 */
public interface WarehouseService {

    /**
     * Tạo mới một kho lưu trữ.
     *
     * @param request Dữ liệu cần thiết để tạo kho mới.
     * @return WarehouseResponse chứa thông tin của kho mới được tạo.
     */
    WarehouseResponse createWarehouse(WarehouseRequest request);

    /**
     * Cập nhật thông tin của một kho lưu trữ.
     *
     * @param id      ID của kho cần cập nhật.
     * @param request Dữ liệu cần cập nhật.
     * @return WarehouseResponse chứa thông tin của kho đã được cập nhật.
     */
    WarehouseResponse updateWarehouse(Long id, WarehouseRequest request);

    /**
     * Lấy thông tin chi tiết của một kho lưu trữ theo ID.
     *
     * @param id ID của kho lưu trữ cần lấy thông tin.
     * @return WarehouseResponse chứa thông tin chi tiết của kho.
     */
    WarehouseResponse getWarehouseById(Long id);

    /**
     * Lấy danh sách các kho lưu trữ với phân trang.
     *
     * @param pageable Thông tin phân trang.
     * @return PageDTO chứa danh sách các kho lưu trữ.
     */
    PageDTO<WarehouseResponse> getAllWarehouses(Pageable pageable);

    /**
     * Xóa một kho lưu trữ theo ID.
     *
     * @param id ID của kho cần xóa.
     */
    void deleteWarehouse(Long id);
}
