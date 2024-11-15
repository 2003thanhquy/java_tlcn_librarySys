package com.spkt.librasys.service;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.request.shelf.ShelfRequest;
import com.spkt.librasys.dto.response.shelf.ShelfResponse;
import org.springframework.data.domain.Pageable;

public interface ShelfService {

    ShelfResponse createShelf(ShelfRequest request);

    ShelfResponse updateShelf(Long id, ShelfRequest request);

    ShelfResponse getShelfById(Long id);

    PageDTO<ShelfResponse> getAllShelves(Pageable pageable);

    void deleteShelf(Long id);
}
