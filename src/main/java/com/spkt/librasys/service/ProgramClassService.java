package com.spkt.librasys.service;

import org.springframework.web.multipart.MultipartFile;

public interface ProgramClassService {
    void saveProgramClassesFromExcel(MultipartFile file);
}
