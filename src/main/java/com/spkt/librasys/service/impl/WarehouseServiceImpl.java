package com.spkt.librasys.service.impl;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.request.warehouse.WarehouseRequest;
import com.spkt.librasys.dto.response.warehouse.WarehouseResponse;
import com.spkt.librasys.entity.Warehouse;
import com.spkt.librasys.exception.AppException;
import com.spkt.librasys.exception.ErrorCode;
import com.spkt.librasys.mapper.WarehouseMapper;
import com.spkt.librasys.repository.WarehouseRepository;
import com.spkt.librasys.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    @Override
    public WarehouseResponse createWarehouse(WarehouseRequest request) {
        Warehouse warehouse = warehouseMapper.toEntity(request);
        Warehouse savedWarehouse = warehouseRepository.save(warehouse);
        return warehouseMapper.toResponse(savedWarehouse);
    }

    @Override
    public WarehouseResponse updateWarehouse(Long id, WarehouseRequest request) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Warehouse not found"));

        warehouse.setWarehouseName(request.getWarehouseName());
        warehouse.setLocation(request.getLocation());

        Warehouse updatedWarehouse = warehouseRepository.save(warehouse);
        return warehouseMapper.toResponse(updatedWarehouse);
    }

    @Override
    public WarehouseResponse getWarehouseById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Warehouse not found"));

        return warehouseMapper.toResponse(warehouse);
    }

    @Override
    public PageDTO<WarehouseResponse> getAllWarehouses(Pageable pageable) {
        Page<Warehouse> warehousePage = warehouseRepository.findAll(pageable);
        return new PageDTO<>(warehousePage.map(warehouseMapper::toResponse));
    }

    @Override
    public void deleteWarehouse(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Warehouse not found"));

        warehouseRepository.delete(warehouse);
    }
}
