package com.spkt.librasys.repository;

import com.spkt.librasys.entity.DocumentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentHistoryRepository extends JpaRepository<DocumentHistory, Long> {
    // Thêm các phương thức truy vấn tùy chỉnh nếu cần
}