package com.spkt.librasys.controller;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.request.course.CourseRequest;
import com.spkt.librasys.dto.response.ApiResponse;
import com.spkt.librasys.dto.response.course.CourseResponse;
import com.spkt.librasys.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/courses")
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ApiResponse<CourseResponse> createCourse(@Valid @RequestBody CourseRequest request) {
        CourseResponse response = courseService.createCourse(request);
        return ApiResponse.<CourseResponse>builder()
                .message("Course created successfully")
                .result(response)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<CourseResponse> updateCourse(@PathVariable Long id, @Valid @RequestBody CourseRequest request) {
        CourseResponse response = courseService.updateCourse(id, request);
        return ApiResponse.<CourseResponse>builder()
                .message("Course updated successfully")
                .result(response)
                .build();
    }

    @GetMapping
    public ApiResponse<PageDTO<CourseResponse>> getAllCourses(Pageable pageable) {
        PageDTO<CourseResponse> response = courseService.getAllCourses(pageable);
        return ApiResponse.<PageDTO<CourseResponse>>builder()
                .message("Courses retrieved successfully")
                .result(response)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<CourseResponse> getCourseById(@PathVariable Long id) {
        CourseResponse response = courseService.getCourseById(id);
        return ApiResponse.<CourseResponse>builder()
                .message("Course retrieved successfully")
                .result(response)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ApiResponse.<Void>builder()
                .message("Course deleted successfully")
                .build();
    }

    @PostMapping("/upload")
    public ApiResponse<Void> createCoursesFromExcel(@RequestParam("file") MultipartFile file) {
        courseService.createCoursesFromExcel(file);
        return ApiResponse.<Void>builder()
                .message("Courses uploaded successfully")
                .build();
    }
}
