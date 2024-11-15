package com.spkt.librasys.service;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.request.displayzone.DisplayZoneRequest;
import com.spkt.librasys.dto.response.displayzone.DisplayZoneResponse;
import org.springframework.data.domain.Pageable;

public interface DisplayZoneService {

    DisplayZoneResponse createDisplayZone(DisplayZoneRequest request);

    DisplayZoneResponse updateDisplayZone(Long id, DisplayZoneRequest request);

    DisplayZoneResponse getDisplayZoneById(Long id);

    PageDTO<DisplayZoneResponse> getAllDisplayZones(Pageable pageable);

    void deleteDisplayZone(Long id);
}
