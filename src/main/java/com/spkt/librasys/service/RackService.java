package com.spkt.librasys.service;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.request.rack.RackRequest;
import com.spkt.librasys.dto.response.rack.RackResponse;
import org.springframework.data.domain.Pageable;

public interface RackService {

    RackResponse createRack(RackRequest request);

    RackResponse updateRack(Long id, RackRequest request);

    RackResponse getRackById(Long id);

    PageDTO<RackResponse> getAllRacks(Pageable pageable);

    void deleteRack(Long id);
}
