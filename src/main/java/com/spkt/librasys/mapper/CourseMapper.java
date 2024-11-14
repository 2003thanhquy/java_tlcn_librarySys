package com.spkt.librasys.mapper;
import com.spkt.librasys.dto.response.course.CourseResponse;
import com.spkt.librasys.entity.Course;
import com.spkt.librasys.entity.Document;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    CourseResponse toCourseResponse(Course course);
}
