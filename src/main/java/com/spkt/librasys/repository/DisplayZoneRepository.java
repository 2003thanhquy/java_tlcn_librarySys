package com.spkt.librasys.repository;

import com.spkt.librasys.entity.DisplayZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisplayZoneRepository extends JpaRepository<DisplayZone, Long> {
    // Các phương thức tùy chỉnh nếu cần
}
