package com.spkt.librasys.repository;

import com.spkt.librasys.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByDepartmentCode(String name);
    Optional<Department> findByDepartmentCodeId(Long departmentCodeId);
}
