package com.spkt.librasys.service;

import com.spkt.librasys.dto.response.accessHistory.AccessHistoryResponse;
import com.spkt.librasys.entity.AccessHistory;
import com.spkt.librasys.entity.Document;
import com.spkt.librasys.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccessHistoryService {
    void recordAccess(User user, Document document, AccessHistory.Activity activity);
    Page<AccessHistoryResponse> getAllAccessHistories(Pageable pageable);
    AccessHistoryResponse getAccessHistoryById(Long id);
    Page<AccessHistoryResponse> searchAccessHistories(String userId, Long documentId, String activity, String fromDate, String toDate, Pageable pageable);
    void deleteAccessHistoryById(Long id);
}

