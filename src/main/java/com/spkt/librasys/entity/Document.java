package com.spkt.librasys.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

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

    @Column(name = "document_name", nullable = false)
    String documentName;

    @Column(name = "author", nullable = false)
    String author;

    @Column(name = "publisher")
    String publisher;

    @Column(name = "published_date")
    LocalDate publishedDate;

    @Column(name = "page_count")
    int pageCount;

    @Column(name = "quantity")
    int quantity;

    @Column(name = "description", length = 1000)
    String description;

    @Column(name = "document_link")
    String documentLink;

    // Nhiều document thuộc một document type
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_type_id") // Khóa ngoại trong bảng documents_001
    @ToString.Exclude
    DocumentType documentType;

    // Một document có nhiều loan transactions (mappedBy trỏ tới trường 'document' trong LoanTransaction)
    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    List<LoanTransaction> loanTransactions;

    // Một document có nhiều access histories (mappedBy trỏ tới trường 'document' trong AccessHistory)
    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    List<AccessHistory> accessHistories;
}

