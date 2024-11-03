package com.spkt.librasys.entity;

import com.spkt.librasys.entity.enums.DocumentSize;
import com.spkt.librasys.entity.enums.DocumentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "documents_001") // Bảng tương ứng trong DB là documents_001
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id", nullable = false, unique = true)
    Long documentId;

    // Thông tin cơ bản về tài liệu
    @Column(name = "isbn", unique = true, nullable = false)
    String isbn; // Mã ISBN của tài liệu

    @Column(name = "document_name", nullable = false)
    String documentName; // Tên của tài liệu

    @Column(name = "author", nullable = false)
    String author; // Tên tác giả

    @Column(name = "publisher")
    String publisher; // Nhà xuất bản

    @Column(name = "published_date")
    LocalDate publishedDate; // Ngày xuất bản

    @Column(name = "page_count")
    int pageCount; // Số trang của tài liệu

    @Column(name = "language")
    String language; // Ngôn ngữ của tài liệu

    // Quản lý số lượng và trạng thái
    @Column(name = "quantity", nullable = false)
    int quantity; // Tổng số lượng sách trong thư viện

    @Column(name = "available_count", nullable = false)
    int availableCount; // Số lượng sách có sẵn để mượn

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    DocumentStatus status  = DocumentStatus.AVAILABLE; // Trạng thái của sách

    // Thông tin bổ sung
    @Column(name = "description", length = 1000)
    String description; // Mô tả ngắn về tài liệu

    @Column(name = "cover_image")
    String coverImage; // Đường dẫn tới ảnh bìa của tài liệu

    @Column(name = "document_link")
    String documentLink; // Đường dẫn tới tài liệu điện tử (nếu có)

    @Column(name = "price", precision = 19, scale = 2)
    BigDecimal price;

    // Thuộc tính kích thước của tài liệu
    @Enumerated(EnumType.STRING)
    @Column(name = "size", nullable = false)
    private DocumentSize size; // Kích thước của sách

    // Quan hệ Many-to-Many với DocumentType
    @ManyToMany
    @JoinTable(
            name = "document_document_types", // Tên bảng liên kết
            joinColumns = @JoinColumn(name = "document_id"),
            inverseJoinColumns = @JoinColumn(name = "document_type_id")
    )
    Set<DocumentType> documentTypes = new HashSet<>();

//    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
//    @ToString.Exclude
//    List<LoanTransaction> loanTransactions; // Các giao dịch mượn liên quan đến tài liệu

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    List<AccessHistory> accessHistories; // Lịch sử truy cập của tài liệu

    // Element collection cho danh sách các vị trí lưu trữ và số lượng tại mỗi vị trí
    @ElementCollection
    @CollectionTable(name = "document_locations", joinColumns = @JoinColumn(name = "document_id"))
    @Builder.Default
    private List<DocumentLocation> locations = new ArrayList<>();

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    List<DocumentHistory> documentHistories = new ArrayList<>();
}
