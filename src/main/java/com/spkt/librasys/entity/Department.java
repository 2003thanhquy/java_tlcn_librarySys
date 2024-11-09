package com.spkt.librasys.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_id", nullable = false, unique = true)
    Long departmentId;

    @Column(name = "department_code_id", nullable = false, unique = true)
    Long departmentCodeId;

    @Column(name = "department_code", nullable = false)
    String departmentCode; // Mã ngành, ví dụ: "CS", "ECON"

    @Column(name = "department_name", nullable = false)
    String departmentName; // Tên ngành, ví dụ: "Công nghệ Thông tin"

    @Column(name = "description", length = 1000)
    String description; // Mô tả ngành học

    // Quan hệ One-to-Many với User
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    Set<User> users;

    // Quan hệ One-to-Many với StudentClass
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    Set<ProgramClass> programClasses;
}
