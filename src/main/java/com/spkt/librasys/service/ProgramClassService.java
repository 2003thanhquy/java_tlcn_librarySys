package com.spkt.librasys.service;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.response.programclass.ProgramClassResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Giao diện ProgramClassService định nghĩa các hành vi liên quan đến chương trình lớp học (ProgramClass).
 */
public interface ProgramClassService {

    /**
     * Lưu danh sách ProgramClass từ file Excel.
     *
     * @param file File Excel chứa dữ liệu ProgramClass
     */
    void saveProgramClassesFromExcel(MultipartFile file);

    /**
     * Lấy thông tin ProgramClass theo ID.
     *
     * @param id ID của ProgramClass
     * @return Thông tin chi tiết của ProgramClass
     */
    ProgramClassResponse getProgramClassById(Long id);

    /**
     * Lấy danh sách ProgramClass (phân trang).
     *
     * @param pageable Thông tin phân trang
     * @return Danh sách ProgramClass được phân trang
     */
    Page<ProgramClassResponse> getAllProgramClasses(Pageable pageable);

    /**
     * Tạo mới một ProgramClass.
     *
     * @param request Dữ liệu của ProgramClass mới
     * @return ProgramClass vừa được tạo
     */
    ProgramClassResponse createProgramClass(ProgramClassResponse request);

    /**
     * Cập nhật thông tin của một ProgramClass.
     *
     * @param id      ID của ProgramClass cần cập nhật
     * @param request Thông tin mới cần cập nhật
     * @return ProgramClass sau khi cập nhật
     */
    ProgramClassResponse updateProgramClass(Long id, ProgramClassResponse request);

    /**
     * Xóa một ProgramClass theo ID.
     *
     * @param id ID của ProgramClass cần xóa
     */
    void deleteProgramClass(Long id);

    /**
     * Xóa danh sách ProgramClass theo ID.
     *
     * @param ids Danh sách ID của ProgramClass cần xóa
     */
    void deleteProgramClasses(List<Long> ids);
}
