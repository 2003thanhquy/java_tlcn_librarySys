package com.spkt.librasys.repository;

import com.spkt.librasys.entity.Shelf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShelfRepository extends JpaRepository<Shelf, Long> {
    // Các phương thức tùy chỉnh nếu cần
}
