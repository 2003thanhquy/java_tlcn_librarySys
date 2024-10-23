package com.spkt.librasys.service.impl;

import com.spkt.librasys.dto.response.fine.FineResponse;
import com.spkt.librasys.entity.Fine;
import com.spkt.librasys.exception.AppException;
import com.spkt.librasys.exception.ErrorCode;
import com.spkt.librasys.mapper.FineMapper;
import com.spkt.librasys.repository.FineRepository;
import com.spkt.librasys.service.FineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FineServiceImpl implements FineService {

    private final FineRepository fineRepository;
    private final FineMapper fineMapper;

    @Override
    public Page<FineResponse> getAllFines(Pageable pageable) {
        Page<Fine> fines = fineRepository.findAll(pageable);
        return fines.map(fineMapper::toFineResponse);
    }

    @Override
    public void payFine(Long fineId) {
        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(() -> new AppException(ErrorCode.FINE_NOT_FOUND));
        fine.setStatus(Fine.Status.PAID);
        fineRepository.save(fine);
    }
}
