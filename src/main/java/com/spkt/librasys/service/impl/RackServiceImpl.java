package com.spkt.librasys.service.impl;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.request.rack.RackRequest;
import com.spkt.librasys.dto.response.rack.RackResponse;
import com.spkt.librasys.entity.Rack;
import com.spkt.librasys.entity.Shelf;
import com.spkt.librasys.exception.AppException;
import com.spkt.librasys.exception.ErrorCode;
import com.spkt.librasys.mapper.RackMapper;
import com.spkt.librasys.repository.RackRepository;
import com.spkt.librasys.repository.ShelfRepository;
import com.spkt.librasys.service.RackService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RackServiceImpl implements RackService {

    private final RackRepository rackRepository;
    private final ShelfRepository shelfRepository;
    private final RackMapper rackMapper;

    @Override
    public RackResponse createRack(RackRequest request) {
        Shelf shelf = shelfRepository.findById(request.getShelfId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Shelf not found"));

        Rack rack = rackMapper.toEntity(request);
        rack.setShelf(shelf);

        Rack savedRack = rackRepository.save(rack);
        return rackMapper.toResponse(savedRack);
    }

    @Override
    public RackResponse updateRack(Long id, RackRequest request) {
        Rack rack = rackRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Rack not found"));

        Shelf shelf = shelfRepository.findById(request.getShelfId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Shelf not found"));

        rack.setRackNumber(request.getRackNumber());
        rack.setCapacity(request.getCapacity());
        rack.setShelf(shelf);

        Rack updatedRack = rackRepository.save(rack);
        return rackMapper.toResponse(updatedRack);
    }

    @Override
    public RackResponse getRackById(Long id) {
        Rack rack = rackRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Rack not found"));

        return rackMapper.toResponse(rack);
    }

    @Override
    public PageDTO<RackResponse> getAllRacks(Pageable pageable) {
        Page<Rack> rackPage = rackRepository.findAll(pageable);
        return new PageDTO<>(rackPage.map(rackMapper::toResponse));
    }

    @Override
    public void deleteRack(Long id) {
        Rack rack = rackRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Rack not found"));

        rackRepository.delete(rack);
    }
}
