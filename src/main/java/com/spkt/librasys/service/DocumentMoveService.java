package com.spkt.librasys.service;

import com.spkt.librasys.dto.request.document.DocumentMoveRequest;
import com.spkt.librasys.entity.DocumentLocation;
import org.springframework.stereotype.Service;

/**
 * Interface cung cấp các phương thức để di chuyển tài liệu trong hệ thống.
 */
@Service
public interface DocumentMoveService {

    /**
     * Di chuyển tài liệu từ kệ sang kệ khác.
     *
     * @param request Thông tin di chuyển tài liệu.
     */
    void moveDocumentRack(DocumentMoveRequest request);

    /**
     * Di chuyển tài liệu từ kệ sang kho.
     *
     * @param request Thông tin di chuyển tài liệu.
     */
    void moveDocumentToWarehouse(DocumentMoveRequest request);

    /**
     * Phê duyệt và di chuyển tài liệu đến vị trí mới.
     *
     * @param documentId ID của tài liệu.
     * @param quantity Số lượng tài liệu cần di chuyển.
     * @return Đối tượng `DocumentLocation` chứa thông tin vị trí mới của tài liệu.
     */
    DocumentLocation approveAndMoveDocument(Long documentId, int quantity);
}
