package com.spkt.librasys.service;

import com.spkt.librasys.entity.User;

/**
 * Giao diện SecurityContextService định nghĩa các hành vi liên quan đến việc truy xuất thông tin người dùng hiện tại từ security context.
 */
public interface SecurityContextService {

    /**
     * Lấy thông tin người dùng hiện tại từ security context.
     *
     * @return Đối tượng {@link User} chứa thông tin của người dùng hiện tại.
     */
    User getCurrentUser();
}
