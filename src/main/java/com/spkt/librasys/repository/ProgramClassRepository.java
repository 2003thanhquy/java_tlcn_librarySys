package com.spkt.librasys.repository;

import com.spkt.librasys.entity.Department;
import com.spkt.librasys.entity.ProgramClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramClassRepository extends JpaRepository<ProgramClass, Long> {
    // Các phương thức tùy chỉnh nếu cần
    /**
     * Finds ProgramClasses by department code and student batch.
     *
     * @param departmentCode The code of the department.
     * @param studentBatch   The student batch.
     * @return A list of matching ProgramClass entities.
     */
    Page<ProgramClass> findByDepartment_DepartmentCodeAndStudentBatch(String departmentCode, int studentBatch, Pageable pageable);

    /**
     * Tìm các ProgramClass theo studentBatch và Department.
     *
     * @param studentBatch Khóa học sinh viên.
     * @param department Phòng ban.
     * @return Danh sách các ProgramClass phù hợp.
     */
    List<ProgramClass> findByStudentBatchAndDepartment(int studentBatch, Department department);

}
