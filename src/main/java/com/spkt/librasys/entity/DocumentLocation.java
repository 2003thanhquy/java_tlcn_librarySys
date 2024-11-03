package com.spkt.librasys.entity;

import com.spkt.librasys.entity.enums.DocumentSize;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class DocumentLocation {

    @Column(name = "warehouse_id")
    private Long warehouseId; // ID kho (nếu sách lưu trữ tại kho)

    @Column(name = "rack_id")
    private Long rackId; // ID Rack (kệ đẩy)

    @Column(name = "quantity", nullable = false)
    private int quantity; // Số lượng sách tại vị trí này

    @Enumerated(EnumType.STRING)
    @Column(name = "size", nullable = false)
    private DocumentSize size; // Kích thước của sách tại vị trí này

    @Column(name = "total_size", nullable = false)
    private double totalSize; // Tổng kích thước của các sách tại vị trí này

    /**
     * Phương thức để cập nhật tổng kích thước dựa trên số lượng và kích thước của sách
     */
    public void updateTotalSize() {
        this.totalSize = this.quantity * this.size.getSizeValue();
    }

    @PrePersist
    @PreUpdate
    private void prePersistUpdate() {
        updateTotalSize();
        if (!isValid()) {
            throw new RuntimeException("DocumentLocation phải chỉ chứa thông tin về Rack hoặc Warehouse, nhưng không cả hai.");
        }
    }

    /**
     * Kiểm tra tính hợp lệ của DocumentLocation
     * Chỉ chứa thông tin về Rack hoặc Warehouse, không được chứa cả hai
     * @return true nếu hợp lệ, false nếu không
     */
    public boolean isValid() {
        boolean hasRackInfo = (rackId != null);
        boolean hasWarehouseInfo = (warehouseId != null);
        return hasRackInfo ^ hasWarehouseInfo; // XOR: chỉ một trong hai đúng
    }
}
