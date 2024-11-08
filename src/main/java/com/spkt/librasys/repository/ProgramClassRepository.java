package com.spkt.librasys.repository;

import com.spkt.librasys.entity.ProgramClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgramClassRepository extends JpaRepository<ProgramClass, Long> {
    // Các phương thức tùy chỉnh nếu cần
}
