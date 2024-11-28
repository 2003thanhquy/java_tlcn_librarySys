package com.spkt.librasys.service;

import com.spkt.librasys.dto.response.fine.FineResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interface cung cấp các phương thức để quản lý các khoản phạt trong hệ thống thư viện.
 */
public interface FineService {

    /**
     * Lấy danh sách tất cả các khoản phạt với phân trang.
     *
     * @param pageable Thông tin phân trang.
     * @return Trang chứa các khoản phạt.
     */
    Page<FineResponse> getAllFines(Pageable pageable);

    /**
     * Thanh toán một khoản phạt theo ID.
     *
     * @param fineId ID của khoản phạt cần thanh toán.
     */
    void payFine(Long fineId);
}
