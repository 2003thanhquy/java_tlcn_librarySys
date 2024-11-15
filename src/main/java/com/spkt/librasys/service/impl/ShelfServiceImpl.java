package com.spkt.librasys.service.impl;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.request.shelf.ShelfRequest;
import com.spkt.librasys.dto.response.shelf.ShelfResponse;
import com.spkt.librasys.entity.DisplayZone;
import com.spkt.librasys.entity.Shelf;
import com.spkt.librasys.exception.AppException;
import com.spkt.librasys.exception.ErrorCode;
import com.spkt.librasys.mapper.ShelfMapper;
import com.spkt.librasys.repository.DisplayZoneRepository;
import com.spkt.librasys.repository.ShelfRepository;
import com.spkt.librasys.service.ShelfService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShelfServiceImpl implements ShelfService {

    private final ShelfRepository shelfRepository;
    private final DisplayZoneRepository displayZoneRepository;
    private final ShelfMapper shelfMapper;

    @Override
    public ShelfResponse createShelf(ShelfRequest request) {
        DisplayZone zone = displayZoneRepository.findById(request.getZoneId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "DisplayZone not found"));

        Shelf shelf = shelfMapper.toEntity(request);
        shelf.setZone(zone);

        Shelf savedShelf = shelfRepository.save(shelf);
        return shelfMapper.toResponse(savedShelf);
    }

    @Override
    public ShelfResponse updateShelf(Long id, ShelfRequest request) {
        Shelf shelf = shelfRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Shelf not found"));

        DisplayZone zone = displayZoneRepository.findById(request.getZoneId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "DisplayZone not found"));

        shelf.setShelfNumber(request.getShelfNumber());
        shelf.setZone(zone);

        Shelf updatedShelf = shelfRepository.save(shelf);
        return shelfMapper.toResponse(updatedShelf);
    }

    @Override
    public ShelfResponse getShelfById(Long id) {
        Shelf shelf = shelfRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Shelf not found"));

        return shelfMapper.toResponse(shelf);
    }

    @Override
    public PageDTO<ShelfResponse> getAllShelves(Pageable pageable) {
        Page<Shelf> shelfPage = shelfRepository.findAll(pageable);
        return new PageDTO<>(shelfPage.map(shelfMapper::toResponse));
    }

    @Override
    public void deleteShelf(Long id) {
        Shelf shelf = shelfRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Shelf not found"));

        shelfRepository.delete(shelf);
    }
}
