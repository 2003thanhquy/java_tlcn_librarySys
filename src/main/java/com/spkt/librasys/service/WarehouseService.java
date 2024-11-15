package com.spkt.librasys.service;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.request.warehouse.WarehouseRequest;
import com.spkt.librasys.dto.response.warehouse.WarehouseResponse;
import org.springframework.data.domain.Pageable;

public interface WarehouseService {

    WarehouseResponse createWarehouse(WarehouseRequest request);

    WarehouseResponse updateWarehouse(Long id, WarehouseRequest request);

    WarehouseResponse getWarehouseById(Long id);

    PageDTO<WarehouseResponse> getAllWarehouses(Pageable pageable);

    void deleteWarehouse(Long id);
}
