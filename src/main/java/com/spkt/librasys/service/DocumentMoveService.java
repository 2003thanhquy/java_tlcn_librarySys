package com.spkt.librasys.service;

import com.spkt.librasys.dto.request.document.DocumentMoveRequest;
import com.spkt.librasys.entity.DocumentLocation;
import org.springframework.stereotype.Service;

@Service
public interface DocumentMoveService {
    void moveDocumentRack(DocumentMoveRequest request);
    void moveDocumentToWarehouse(DocumentMoveRequest request);
    DocumentLocation approveAndMoveDocument(Long documentId, int quantity);
}