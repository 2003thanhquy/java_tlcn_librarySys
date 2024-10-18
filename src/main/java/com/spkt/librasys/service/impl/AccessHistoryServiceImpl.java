package com.spkt.librasys.service.impl;

import com.spkt.librasys.dto.response.accessHistory.AccessHistoryResponse;
import com.spkt.librasys.entity.AccessHistory;
import com.spkt.librasys.entity.Document;
import com.spkt.librasys.entity.User;
import com.spkt.librasys.exception.AppException;
import com.spkt.librasys.exception.ErrorCode;
import com.spkt.librasys.mapper.AccessHistoryMapper;
import com.spkt.librasys.repository.AccessHistoryRepository;
import com.spkt.librasys.service.AccessHistoryService;
import com.spkt.librasys.repository.specification.AccessHistorySpecification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccessHistoryServiceImpl implements AccessHistoryService {

    AccessHistoryRepository accessHistoryRepository;
    AccessHistoryMapper accessHistoryMapper;
    @Override
    public void recordAccess(User user, Document document, String activity) {
        if (user == null || document == null || activity == null || activity.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }
        AccessHistory accessHistory = AccessHistory.builder()
                .user(user)
                .document(document)
                .activity(activity)
                .accessTime(LocalDateTime.now())
                .build();
        accessHistoryRepository.save(accessHistory);
    }

    @Override
    public Page<AccessHistoryResponse> getAllAccessHistories(Pageable pageable) {
        return accessHistoryRepository.findAll(pageable)
                .map(accessHistoryMapper::toAccessHistoryResponse);
    }

    @Override
    public AccessHistoryResponse getAccessHistoryById(Long id) {
        AccessHistory accessHistory = accessHistoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));
        return accessHistoryMapper.toAccessHistoryResponse(accessHistory);
    }

    @Override
    public Page<AccessHistoryResponse> searchAccessHistories(String userId, Long documentId, String activity, String fromDate, String toDate, Pageable pageable) {
        Specification<AccessHistory> spec = Specification
                .where(AccessHistorySpecification.hasUserId(userId))
                .and(AccessHistorySpecification.hasDocumentId(documentId))
                .and(AccessHistorySpecification.hasActivity(activity))
                .and(AccessHistorySpecification.hasAccessTimeBetween(fromDate, toDate));

        return accessHistoryRepository.findAll(spec, pageable)
                .map(accessHistoryMapper::toAccessHistoryResponse);
    }

    @Override
    public void deleteAccessHistoryById(Long id) {
        Optional<AccessHistory> accessHistory = accessHistoryRepository.findById(id);
        if (accessHistory.isPresent()) {
            accessHistoryRepository.delete(accessHistory.get());
        } else {
            throw new AppException(ErrorCode.DOCUMENT_NOT_FOUND);
        }
    }
}
