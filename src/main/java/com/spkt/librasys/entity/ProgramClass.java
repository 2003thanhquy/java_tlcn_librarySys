package com.spkt.librasys.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "program_classes")
public class ProgramClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_id", nullable = false, unique = true)
    Long classId;

    @Column(name = "year", nullable = false)
    String year; // Năm học, ví dụ: "2023-2024"

    @Column(name = "semester", nullable = false)
    int semester; // Học kỳ, ví dụ: 1, 2

    @Column(name = "student_batch", nullable = false)
    int studentBatch; // Khóa học, ví dụ: 21 (tương ứng với K21), 22 (tương ứng với K22)

    // Quan hệ Many-to-One với Department
    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    Department department;

    // Quan hệ Many-to-Many với Course
    @ManyToMany
    @JoinTable(
            name = "class_courses",
            joinColumns = @JoinColumn(name = "class_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    @Builder.Default
    Set<Course> courses = new HashSet<>();

    // Phương thức để thêm Course vào ProgramClass
    public void addCourse(Course course) {
        this.courses.add(course);               // Thêm khóa học vào danh sách
        course.getProgramClasses().add(this);   // Đảm bảo mối quan hệ hai chiều với Course
    }

    public void removeCourse(Course course) {
        this.courses.remove(course);
        course.getProgramClasses().remove(this);
    }
}
