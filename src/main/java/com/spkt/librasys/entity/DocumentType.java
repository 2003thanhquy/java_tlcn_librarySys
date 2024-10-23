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
@Entity(name = "document_types_001")
public class DocumentType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_type_id")
    Long documentTypeId;

    @Column(name = "type_name", nullable = false,unique = true)
    String typeName;
    @Column(name = "description", nullable = false)
    String description;
    // Một document type có thể chứa nhiều document
    @OneToMany(mappedBy = "documentType", cascade = CascadeType.ALL)
    List<Document> documents;

}
