package com.spkt.librasys.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "warehouses_001")
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "warehouse_id", nullable = false, unique = true)
    private Long warehouseId;

    @Column(name = "warehouse_name", nullable = false, unique = true)
    private String warehouseName; // Tên kho

    @Column(name = "location", nullable = false)
    private String location; // Vị trí kho



    // Mối quan hệ một-nhiều với DocumentLocation (sách lưu trữ tại kho)
    // Không cần thiết phải liên kết trực tiếp vì thông tin đã nằm trong DocumentLocation
}
