package com.spkt.librasys.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    @Column(name = "description")
    String description;
    // Một DocumentType có thể chứa nhiều Document và ngược lại
    @ManyToMany(mappedBy = "documentTypes")
    @JsonIgnore
    Set<Document> documents = new HashSet<>();

}
