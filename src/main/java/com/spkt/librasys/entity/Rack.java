package com.spkt.librasys.entity;

import com.spkt.librasys.entity.enums.DocumentSize;
import com.spkt.librasys.repository.DocumentRepository;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "racks")
public class Rack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rack_id", nullable = false, unique = true)
    private Long rackId;

    @Column(name = "rack_number", nullable = false)
    private String rackNumber; // Số kệ đẩy

    @Column(name = "capacity", nullable = false)
    private double capacity; // Tổng kích thước tối đa mà Rack có thể chứa (ví dụ: cm³)

    // Mối quan hệ nhiều-một với Shelf
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelf_id", nullable = false)
    @ToString.Exclude
    private Shelf shelf;

    // Mối quan hệ một-nhiều với DocumentLocation thông qua Document
    // Không cần thiết phải liên kết trực tiếp vì thông tin đã nằm trong DocumentLocation

    /**
     * Phương thức để tính tổng kích thước hiện tại của các sách trên Rack
     * @return Tổng kích thước hiện tại (ví dụ: cm³)
     */
//    public double calculateCurrentSize(DocumentRepository documentRepository) {
//        List<Document> documents = documentRepository.findDocumentsByRack(rackId);
//        return documents.stream()
//                .mapToDouble(doc -> doc.getSize().getSizeValue() *
//                        doc.getLocations().stream()
//                                .filter(loc -> rackId.equals(loc.getRackId()))
//                                .mapToInt(DocumentLocation::getQuantity)
//                                .sum())
//                .sum();
//    }
    /**
     * Kiểm tra xem Rack còn chỗ trống để thêm sách với kích thước cụ thể hay không
     * @param size Kích thước của sách muốn thêm
     * @return true nếu còn chỗ trống, false nếu đầy
     */
//    public boolean canAddDocument(DocumentSize size, DocumentRepository documentRepository) {
//        double currentSize = calculateCurrentSize(documentRepository);
//        return (currentSize + size.getSizeValue()) <= capacity;
//    }
//
//
//
//    /**
//     * Phương thức kiểm tra trước khi lưu hoặc cập nhật Rack.
//     */
//    @PrePersist
//    @PreUpdate
//    private void validateCapacity(DocumentRepository documentRepository) {
//        double currentSize = calculateCurrentSize(documentRepository);
//        if (currentSize > capacity) {
//            throw new RuntimeException("Rack " + rackNumber + " đã vượt quá khả năng chứa.");
//        }
//    }
}
