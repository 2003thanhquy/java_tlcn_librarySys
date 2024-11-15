package com.spkt.librasys.service.impl;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.request.displayzone.DisplayZoneRequest;
import com.spkt.librasys.dto.response.displayzone.DisplayZoneResponse;
import com.spkt.librasys.entity.DisplayZone;
import com.spkt.librasys.exception.AppException;
import com.spkt.librasys.exception.ErrorCode;
import com.spkt.librasys.mapper.DisplayZoneMapper;
import com.spkt.librasys.repository.DisplayZoneRepository;
import com.spkt.librasys.service.DisplayZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DisplayZoneServiceImpl implements DisplayZoneService {

    private final DisplayZoneRepository displayZoneRepository;
    private final DisplayZoneMapper displayZoneMapper;

    @Override
    public DisplayZoneResponse createDisplayZone(DisplayZoneRequest request) {
        DisplayZone displayZone = displayZoneMapper.toEntity(request);
        DisplayZone savedZone = displayZoneRepository.save(displayZone);
        return displayZoneMapper.toResponse(savedZone);
    }

    @Override
    public DisplayZoneResponse updateDisplayZone(Long id, DisplayZoneRequest request) {
        DisplayZone displayZone = displayZoneRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "DisplayZone not found"));

        displayZone.setZoneName(request.getZoneName());

        DisplayZone updatedZone = displayZoneRepository.save(displayZone);
        return displayZoneMapper.toResponse(updatedZone);
    }

    @Override
    public DisplayZoneResponse getDisplayZoneById(Long id) {
        DisplayZone displayZone = displayZoneRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "DisplayZone not found"));

        return displayZoneMapper.toResponse(displayZone);
    }

    @Override
    public PageDTO<DisplayZoneResponse> getAllDisplayZones(Pageable pageable) {
        Page<DisplayZone> displayZonePage = displayZoneRepository.findAll(pageable);
        return new PageDTO<>(displayZonePage.map(displayZoneMapper::toResponse));
    }

    @Override
    public void deleteDisplayZone(Long id) {
        DisplayZone displayZone = displayZoneRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "DisplayZone not found"));

        displayZoneRepository.delete(displayZone);
    }
}
