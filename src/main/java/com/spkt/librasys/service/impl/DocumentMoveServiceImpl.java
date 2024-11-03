package com.spkt.librasys.service.impl;

import com.spkt.librasys.dto.request.document.DocumentMoveRequest;
import com.spkt.librasys.entity.*;
import com.spkt.librasys.entity.enums.DocumentStatus;
import com.spkt.librasys.exception.AppException;
import com.spkt.librasys.exception.ErrorCode;
import com.spkt.librasys.repository.DocumentHistoryRepository;
import com.spkt.librasys.repository.DocumentRepository;
import com.spkt.librasys.repository.RackRepository;
import com.spkt.librasys.repository.WarehouseRepository;
import com.spkt.librasys.service.DocumentMoveService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DocumentMoveServiceImpl implements DocumentMoveService {

    DocumentRepository documentRepository;
    WarehouseRepository warehouseRepository;
    RackRepository rackRepository;
    DocumentHistoryRepository documentHistoryRepository;

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public void moveDocument(DocumentMoveRequest request) {
        // 1. Lấy thông tin tài liệu từ Document ID
        Document document = documentRepository.findById(request.getDocumentId())
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND, "Document not found"));

        // 2. Kiểm tra Warehouse tồn tại
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new AppException(ErrorCode.WAREHOUSE_NOT_FOUND, "Warehouse not found"));

        // 3. Kiểm tra Rack tồn tại
        Rack rack = rackRepository.findById(request.getRackId())
                .orElseThrow(() -> new AppException(ErrorCode.RACK_NOT_FOUND, "Rack not found"));

        // 4. Tìm DocumentLocation trong kho
        DocumentLocation warehouseLocation = document.getLocations().stream()
                .filter(loc -> loc.getWarehouseId() != null && loc.getWarehouseId().equals(warehouse.getWarehouseId()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.LOCATION_NOT_FOUND, "Document location in warehouse not found"));

        if (warehouseLocation.getQuantity() < request.getQuantity()) {
            throw new AppException(ErrorCode.INVALID_QUANTITY, "Insufficient quantity in warehouse");
        }

        // 5. Kiểm tra khả năng chứa của Rack với kích thước hiện tại
        double currentRackSize = document.getLocations().stream()
                .filter(loc -> loc.getRackId() != null && loc.getRackId().equals(rack.getRackId()))
                .mapToDouble(loc -> loc.getSize().getSizeValue() * loc.getQuantity())
                .sum();
        double totalSizeToAdd = warehouseLocation.getSize().getSizeValue() * request.getQuantity();
        if ((currentRackSize + totalSizeToAdd) > rack.getCapacity()) {
            double availableCapacity = rack.getCapacity() - currentRackSize;
            int maxQuantity = (int) Math.floor(availableCapacity / warehouseLocation.getSize().getSizeValue());
            throw new AppException(ErrorCode.RACK_CAPACITY_EXCEEDED, "Rack does not have enough capacity. Available capacity: " + availableCapacity + " cm³. Maximum number of documents that can be added: " + maxQuantity);
        }

        // 6. Cập nhật số lượng trong kho
        int remainingQuantity = warehouseLocation.getQuantity() - request.getQuantity();
        if (remainingQuantity == 0) {
            document.getLocations().remove(warehouseLocation);
        } else {
            warehouseLocation.setQuantity(remainingQuantity);
        }

        // 7. Tìm hoặc tạo DocumentLocation cho Rack
        DocumentLocation rackLocation = document.getLocations().stream()
                .filter(loc -> loc.getRackId() != null && loc.getRackId().equals(rack.getRackId()))
                .findFirst()
                .orElseGet(() -> {
                    DocumentLocation newLocation = DocumentLocation.builder()
                            .rackId(rack.getRackId())
                            .warehouseId(null)
                            .quantity(0)
                            .size(warehouseLocation.getSize())
                            .build();
                    document.getLocations().add(newLocation);
                    return newLocation;
                });

        rackLocation.setQuantity(rackLocation.getQuantity() + request.getQuantity());
        rackLocation.updateTotalSize();
        // 8. Cập nhật lịch sử chuyển
        saveDocumentHistory(document, warehouseLocation, -request.getQuantity(), DocumentHistory.Action.MOVE);
        saveDocumentHistory(document, rackLocation, request.getQuantity(), DocumentHistory.Action.MOVE);

        // 9. Lưu Document sau khi cập nhật

        documentRepository.save(document);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public void moveDocumentToWarehouse(DocumentMoveRequest request) {
        // Method to move from rack to warehouse
        Document document = documentRepository.findById(request.getDocumentId())
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND, "Document not found"));

        Rack rack = rackRepository.findById(request.getRackId())
                .orElseThrow(() -> new AppException(ErrorCode.RACK_NOT_FOUND, "Rack not found"));

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new AppException(ErrorCode.WAREHOUSE_NOT_FOUND, "Warehouse not found"));

        DocumentLocation rackLocation = document.getLocations().stream()
                .filter(loc -> loc.getRackId() != null && loc.getRackId().equals(rack.getRackId()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.LOCATION_NOT_FOUND, "Document location in rack not found"));

        if (rackLocation.getQuantity() < request.getQuantity()) {
            throw new AppException(ErrorCode.INVALID_QUANTITY, "Insufficient quantity in rack");
        }

        int remainingQuantity = rackLocation.getQuantity() - request.getQuantity();
        if (remainingQuantity == 0) {
            document.getLocations().remove(rackLocation);
        } else {
            rackLocation.setQuantity(remainingQuantity);
        }

        DocumentLocation warehouseLocation = document.getLocations().stream()
                .filter(loc -> loc.getWarehouseId() != null && loc.getWarehouseId().equals(warehouse.getWarehouseId()))
                .findFirst()
                .orElseGet(() -> {
                    DocumentLocation newLocation = DocumentLocation.builder()
                            .warehouseId(warehouse.getWarehouseId())
                            .rackId(null)
                            .quantity(0)
                            .size(rackLocation.getSize())
                            .build();
                    document.getLocations().add(newLocation);
                    return newLocation;
                });

        warehouseLocation.setQuantity(warehouseLocation.getQuantity() + request.getQuantity());

        // Cập nhật lịch sử chuyển
        saveDocumentHistory(document, rackLocation, -request.getQuantity(), DocumentHistory.Action.MOVE);
        saveDocumentHistory(document, warehouseLocation, request.getQuantity(), DocumentHistory.Action.MOVE);

        // Lưu Document sau khi cập nhật
        documentRepository.save(document);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public DocumentLocation approveAndMoveDocument(Long documentId, int quantity) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND, "Document not found"));

        DocumentLocation rackLocation = document.getLocations().stream()
                .filter(loc -> loc.getRackId() != null && loc.getQuantity() >= quantity)
                .findFirst()
                .orElse(null);

        if (rackLocation != null) {
            rackLocation.setQuantity(rackLocation.getQuantity() - quantity);
            saveDocumentHistory(document, rackLocation, -quantity, DocumentHistory.Action.MOVE);
            documentRepository.save(document);
            return rackLocation;
        }

        DocumentLocation warehouseLocation = document.getLocations().stream()
                .filter(loc -> loc.getWarehouseId() != null && loc.getQuantity() >= quantity)
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_QUANTITY, "Không đủ số lượng sách có sẵn"));

        warehouseLocation.setQuantity(warehouseLocation.getQuantity() - quantity);
        saveDocumentHistory(document, warehouseLocation, -quantity, DocumentHistory.Action.MOVE);
        documentRepository.save(document);
        return warehouseLocation;
    }

    private void saveDocumentHistory(Document document, DocumentLocation location, int quantityChange, DocumentHistory.Action action) {
        DocumentHistory history = DocumentHistory.builder()
                .document(document)
                .location(location)
                .changeTime(LocalDateTime.now())
                .action(action)
                .quantityChange(quantityChange)
                .build();
        documentHistoryRepository.save(history);
    }
}
