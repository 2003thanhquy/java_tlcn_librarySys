package com.spkt.librasys.service;

import com.spkt.librasys.dto.response.ApiResponse;
import com.spkt.librasys.dto.response.programclass.ProgramClassResponse;
import com.spkt.librasys.entity.Course;
import com.spkt.librasys.entity.Department;
import com.spkt.librasys.entity.ProgramClass;
import com.spkt.librasys.exception.AppException;
import com.spkt.librasys.exception.ErrorCode;
import com.spkt.librasys.repository.CourseRepository;
import com.spkt.librasys.repository.DepartmentRepository;
import com.spkt.librasys.repository.ProgramClassRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgramClassService {

    private final ProgramClassRepository programClassRepository;
    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public void saveProgramClassesFromExcel(MultipartFile file) {
        try {
            InputStream is = file.getInputStream();
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0); // Giả sử dữ liệu nằm ở sheet đầu tiên

            String currentYear = null;
            String currentDepartment = null;
            String currentSemester = null;
            String currentBatch = null;

            Iterator<Row> rowIterator = sheet.iterator();

            // Bỏ qua hàng đầu tiên nếu có tiêu đề
            boolean hasHeader = true;
            if (hasHeader && rowIterator.hasNext()) {
                rowIterator.next();
            }

            List<ProgramClass> programClasses = new ArrayList<>();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                Cell yearCell = row.getCell(0);
                Cell batchCell = row.getCell(1);
                Cell departmentCell = row.getCell(2);
                Cell semesterCell = row.getCell(3);
                Cell courseCell = row.getCell(4);

                // Cập nhật giá trị Year nếu ô không rỗng
                if (isCellNotEmpty(yearCell)) {
                    currentYear = getCellStringValue(yearCell);
                }

                // Cập nhật giá trị Batch nếu ô không rỗng
                if (isCellNotEmpty(batchCell)) {
                    currentBatch = getCellStringValue(batchCell);
                }

                // Cập nhật giá trị Department nếu ô không rỗng
                if (isCellNotEmpty(departmentCell)) {
                    currentDepartment = getCellStringValue(departmentCell);
                }

                // Cập nhật giá trị Semester nếu ô không rỗng
                if (isCellNotEmpty(semesterCell)) {
                    currentSemester = getCellStringValue(semesterCell).replace("HK", "").trim();
                }

                // Xử lý Course nếu ô không rỗng
                if (isCellNotEmpty(courseCell)) {
                    String courseCode = getCellStringValue(courseCell);

                    if (currentYear == null) {
                        workbook.close();
                        throw new AppException(ErrorCode.INVALID_REQUEST, "Thiếu thông tin Year tại dòng: " + (row.getRowNum() + 1) + ", cột: 1");
                    }
                    if (currentBatch == null) {
                        workbook.close();
                        throw new AppException(ErrorCode.INVALID_REQUEST, "Thiếu thông tin Batch tại dòng: " + (row.getRowNum() + 1) + ", cột: 2");
                    }
                    if (currentDepartment == null) {
                        workbook.close();
                        throw new AppException(ErrorCode.INVALID_REQUEST, "Thiếu thông tin Department tại dòng: " + (row.getRowNum() + 1) + ", cột: 3");
                    }
                    if (currentSemester == null) {
                        workbook.close();
                        throw new AppException(ErrorCode.INVALID_REQUEST, "Thiếu thông tin Semester tại dòng: " + (row.getRowNum() + 1) + ", cột: 4");
                    }

                    int semester = Integer.parseInt(currentSemester);
                    int batch = Integer.parseInt(currentBatch);

                    String finalCurrentDepartment = currentDepartment;
                    Department department = departmentRepository.findByDepartmentCode(currentDepartment.trim())
                            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy Department với tên: " + finalCurrentDepartment + " tại dòng: " + (row.getRowNum() + 1) + ", cột: 3"));

                    Course course = courseRepository.findByCourseCode(courseCode.trim())
                            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy Course với tên: " + courseCode + " tại dòng: " + (row.getRowNum() + 1) + ", cột: 5"));

                    ProgramClass programClass = ProgramClass.builder()
                            .year(currentYear)
                            .semester(semester)
                            .studentBatch(batch)
                            .department(department)
                            .courses(Set.of(course))
                            .build();

                    programClasses.add(programClass);
                }
            }

            // Thêm các ProgramClass mới với năm/kỳ học tương ứng
            programClassRepository.saveAll(programClasses);
            workbook.close();
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(ErrorCode.SERVER_ERROR, "Lỗi khi xử lý file Excel: " + e.getMessage());
        }
    }

    private boolean isCellNotEmpty(Cell cell) {
        return cell != null && cell.getCellType() != CellType.BLANK && !getCellStringValue(cell).trim().isEmpty();
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                } else {
                    double numericValue = cell.getNumericCellValue();
                    return numericValue == (long) numericValue ? String.valueOf((long) numericValue) : String.valueOf(numericValue);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                return evaluator.evaluate(cell).formatAsString();
            default:
                return "";
        }
    }
    public ProgramClassResponse getProgramClassById(Long id) {
        ProgramClass programClass = programClassRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "ProgramClass không tồn tại"));

        return convertToResponse(programClass);
    }

    public Page<ProgramClassResponse> getAllProgramClasses(Pageable pageable) {
        Page<ProgramClass> programClasses = programClassRepository.findAll(pageable);
        return programClasses.map(this::convertToResponse);
    }

    public ProgramClassResponse createProgramClass(ProgramClassResponse request) {
        ProgramClass programClass = new ProgramClass();
        programClass.setYear(request.getYear());
        programClass.setSemester(request.getSemester());
        programClass.setStudentBatch(request.getStudentBatch());
        // Add Department and Courses logic

        programClass = programClassRepository.save(programClass);
        return convertToResponse(programClass);
    }

    public ProgramClassResponse updateProgramClass(Long id, ProgramClassResponse request) {
        ProgramClass programClass = programClassRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "ProgramClass không tồn tại"));

        programClass.setYear(request.getYear());
        programClass.setSemester(request.getSemester());
        programClass.setStudentBatch(request.getStudentBatch());
        // Update Department and Courses logic

        programClass = programClassRepository.save(programClass);
        return convertToResponse(programClass);
    }

    public void deleteProgramClass(Long id) {
        ProgramClass programClass = programClassRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "ProgramClass không tồn tại"));
        programClassRepository.delete(programClass);
    }

    public void deleteProgramClasses(List<Long> ids) {
        List<ProgramClass> programClasses = programClassRepository.findAllById(ids);
        if (programClasses.size() != ids.size()) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Một hoặc nhiều ProgramClass không tồn tại");
        }
        programClassRepository.deleteAll(programClasses);
    }

    private ProgramClassResponse convertToResponse(ProgramClass programClass) {
        return ProgramClassResponse.builder()
                .id(programClass.getClassId())
                .year(programClass.getYear())
                .semester(programClass.getSemester())
                .studentBatch(programClass.getStudentBatch())
                .departmentName(programClass.getDepartment().getDepartmentName())
                .courseCodes(programClass.getCourses().stream().map(course -> course.getCourseCode()).collect(Collectors.toSet()))
                .build();
    }

}
