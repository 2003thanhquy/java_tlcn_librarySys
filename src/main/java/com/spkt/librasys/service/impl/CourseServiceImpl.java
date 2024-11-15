package com.spkt.librasys.service.impl;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.request.course.CourseRequest;
import com.spkt.librasys.dto.response.course.CourseResponse;
import com.spkt.librasys.entity.Course;
import com.spkt.librasys.exception.AppException;
import com.spkt.librasys.exception.ErrorCode;
import com.spkt.librasys.mapper.CourseMapper;
import com.spkt.librasys.repository.CourseRepository;
import com.spkt.librasys.service.CourseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseServiceImpl implements CourseService {

    CourseRepository courseRepository;
    CourseMapper courseMapper;

    @Override
    public CourseResponse createCourse(CourseRequest request) {
        Course course = courseMapper.toEntity(request);
        Course savedCourse = courseRepository.save(course);
        return courseMapper.toResponse(savedCourse);
    }

    @Override
    public CourseResponse updateCourse(Long id, CourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        course.setCourseCode(request.getCourseCode());
        course.setCourseName(request.getCourseName());
        course.setDescription(request.getDescription());

        Course updatedCourse = courseRepository.save(course);
        return courseMapper.toResponse(updatedCourse);
    }

    @Override
    public PageDTO<CourseResponse> getAllCourses(Pageable pageable) {
        Page<Course> coursePage = courseRepository.findAll(pageable);
        Page<CourseResponse> dtoPage = coursePage.map(courseMapper::toResponse);
        return new PageDTO<>(dtoPage);
    }

    @Override
    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        return courseMapper.toResponse(course);
    }

    @Override
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    @Override
    public void createCoursesFromExcel(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            List<Course> courses = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Bỏ qua tiêu đề
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Course course = Course.builder()
                        .courseCode(row.getCell(0).getStringCellValue())
                        .courseName(row.getCell(1).getStringCellValue())
                        .description(row.getCell(2).getStringCellValue())
                        .build();

                courses.add(course);
            }

            courseRepository.saveAll(courses);
        } catch (Exception e) {
            log.error("Error processing Excel file: {}", e.getMessage());
            throw new AppException(ErrorCode.SERVER_ERROR,"Failed to process Excel file");
        }
    }
}
