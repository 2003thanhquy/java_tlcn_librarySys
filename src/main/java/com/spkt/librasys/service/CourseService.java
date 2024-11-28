package com.spkt.librasys.service;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.request.course.CourseRequest;
import com.spkt.librasys.dto.response.course.CourseResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interface cung cấp các phương thức xử lý liên quan đến việc quản lý khóa học trong hệ thống.
 * Các phương thức này bao gồm tạo mới, cập nhật, lấy thông tin, xóa khóa học và nhập khóa học từ tệp Excel.
 */
public interface CourseService {

    /**
     * Tạo mới một khóa học.
     *
     * @param request Thông tin yêu cầu tạo khóa học (tên khóa học, mô tả, v.v.).
     * @return Phản hồi chứa thông tin chi tiết của khóa học vừa tạo.
     */
    CourseResponse createCourse(CourseRequest request);

    /**
     * Cập nhật thông tin một khóa học.
     *
     * @param id      ID của khóa học cần cập nhật.
     * @param request Thông tin yêu cầu cập nhật khóa học.
     * @return Phản hồi chứa thông tin chi tiết của khóa học sau khi cập nhật.
     */
    CourseResponse updateCourse(Long id, CourseRequest request);

    /**
     * Lấy danh sách tất cả các khóa học, hỗ trợ phân trang.
     *
     * @param pageable Tham số phân trang để lấy danh sách khóa học.
     * @return Danh sách các khóa học được phân trang.
     */
    PageDTO<CourseResponse> getAllCourses(Pageable pageable);

    /**
     * Lấy thông tin chi tiết của một khóa học theo ID.
     *
     * @param id ID của khóa học cần lấy thông tin.
     * @return Phản hồi chứa thông tin chi tiết của khóa học.
     */
    CourseResponse getCourseById(Long id);

    /**
     * Xóa một khóa học theo ID.
     *
     * @param id ID của khóa học cần xóa.
     */
    void deleteCourse(Long id);

    /**
     * Tạo các khóa học từ một tệp Excel.
     *
     * @param file Tệp Excel chứa dữ liệu các khóa học.
     */
    void createCoursesFromExcel(MultipartFile file);
}
