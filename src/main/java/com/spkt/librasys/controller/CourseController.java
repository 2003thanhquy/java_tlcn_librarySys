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

/**
 * Controller xử lý các yêu cầu liên quan đến các khóa học (Course).
 * Cung cấp các API để tạo, cập nhật, lấy, xóa và tải lên danh sách khóa học từ Excel.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/courses")
public class CourseController {

    private final CourseService courseService;

    /**
     * Endpoint để tạo một khóa học mới.
     *
     * @param request Thông tin khóa học cần tạo.
     * @return ApiResponse chứa thông tin của khóa học vừa được tạo.
     */
    @PostMapping
    public ApiResponse<CourseResponse> createCourse(@Valid @RequestBody CourseRequest request) {
        CourseResponse response = courseService.createCourse(request);
        return ApiResponse.<CourseResponse>builder()
                .message("Course created successfully")
                .result(response)
                .build();
    }

    /**
     * Endpoint để cập nhật thông tin một khóa học theo ID.
     *
     * @param id ID của khóa học cần cập nhật.
     * @param request Thông tin cập nhật cho khóa học.
     * @return ApiResponse chứa thông tin của khóa học đã được cập nhật.
     */
    @PutMapping("/{id}")
    public ApiResponse<CourseResponse> updateCourse(@PathVariable Long id, @Valid @RequestBody CourseRequest request) {
        CourseResponse response = courseService.updateCourse(id, request);
        return ApiResponse.<CourseResponse>builder()
                .message("Course updated successfully")
                .result(response)
                .build();
    }

    /**
     * Endpoint để lấy danh sách tất cả các khóa học, có phân trang.
     *
     * @param pageable Tham số phân trang để phân trang kết quả.
     * @return ApiResponse chứa danh sách các khóa học.
     */
    @GetMapping
    public ApiResponse<PageDTO<CourseResponse>> getAllCourses(Pageable pageable) {
        PageDTO<CourseResponse> response = courseService.getAllCourses(pageable);
        return ApiResponse.<PageDTO<CourseResponse>>builder()
                .message("Courses retrieved successfully")
                .result(response)
                .build();
    }

    /**
     * Endpoint để lấy thông tin chi tiết của một khóa học theo ID.
     *
     * @param id ID của khóa học cần lấy.
     * @return ApiResponse chứa thông tin chi tiết của khóa học.
     */
    @GetMapping("/{id}")
    public ApiResponse<CourseResponse> getCourseById(@PathVariable Long id) {
        CourseResponse response = courseService.getCourseById(id);
        return ApiResponse.<CourseResponse>builder()
                .message("Course retrieved successfully")
                .result(response)
                .build();
    }

    /**
     * Endpoint để xóa một khóa học theo ID.
     *
     * @param id ID của khóa học cần xóa.
     * @return ApiResponse thông báo xóa thành công.
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ApiResponse.<Void>builder()
                .message("Course deleted successfully")
                .build();
    }

    /**
     * Endpoint để tạo nhiều khóa học từ tệp Excel.
     *
     * @param file Tệp Excel chứa danh sách các khóa học cần tạo.
     * @return ApiResponse thông báo thành công sau khi tải lên tệp.
     */
    @PostMapping("/upload")
    public ApiResponse<Void> createCoursesFromExcel(@RequestParam("file") MultipartFile file) {
        courseService.createCoursesFromExcel(file);
        return ApiResponse.<Void>builder()
                .message("Courses uploaded successfully")
                .build();
    }
}
