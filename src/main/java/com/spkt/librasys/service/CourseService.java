package com.spkt.librasys.service;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.request.course.CourseRequest;
import com.spkt.librasys.dto.response.course.CourseResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface CourseService {
    CourseResponse createCourse(CourseRequest request);
    CourseResponse updateCourse(Long id, CourseRequest request);
    PageDTO<CourseResponse> getAllCourses(Pageable pageable);
    CourseResponse getCourseById(Long id);
    void deleteCourse(Long id);
    void createCoursesFromExcel(MultipartFile file);
}
