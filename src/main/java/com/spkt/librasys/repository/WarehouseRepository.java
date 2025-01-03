package com.spkt.librasys.repository;

import com.spkt.librasys.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    Optional<Warehouse> findByWarehouseName(String warehouseName);

    // Ví dụ: Tìm Warehouse chính
    default Optional<Warehouse> findMainWarehouse() {
        return findByWarehouseName("Main Warehouse");
    }
}
