package com.spkt.librasys.service;

import com.spkt.librasys.dto.response.fine.FineResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FineService {
    Page<FineResponse> getAllFines(Pageable pageable);
    void payFine(Long fineId);
}
