package com.spkt.librasys.mapper;

import com.spkt.librasys.dto.response.department.DepartmentResponse;
import com.spkt.librasys.entity.Department;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {
    DepartmentResponse toDepartmentResponse(Department department);
}
