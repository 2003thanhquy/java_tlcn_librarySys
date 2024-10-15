package com.spkt.librasys.repository.specification;

import com.spkt.librasys.entity.AccessHistory;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class AccessHistorySpecification {

    public static Specification<AccessHistory> hasUserId(String userId) {
        return (root, query, criteriaBuilder) -> {
            if (userId == null || userId.trim().isEmpty()) {
                return null;
            }
            return criteriaBuilder.equal(root.get("user").get("userId"), userId);
        };
    }

    public static Specification<AccessHistory> hasDocumentId(Long documentId) {
        return (root, query, criteriaBuilder) -> {
            if (documentId == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("document").get("documentId"), documentId);
        };
    }

    public static Specification<AccessHistory> hasActivity(String activity) {
        return (root, query, criteriaBuilder) -> {
            if (activity == null || activity.trim().isEmpty()) {
                return null;
            }
            return criteriaBuilder.like(root.get("activity"), "%" + activity + "%");
        };
    }

    public static Specification<AccessHistory> hasAccessTimeBetween(String fromDate, String toDate) {
        return (root, query, criteriaBuilder) -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime from = Optional.ofNullable(fromDate).map(d -> LocalDateTime.parse(d, formatter)).orElse(null);
            LocalDateTime to = Optional.ofNullable(toDate).map(d -> LocalDateTime.parse(d, formatter)).orElse(null);

            if (from != null && to != null) {
                return criteriaBuilder.between(root.get("accessTime"), from, to);
            } else if (from != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("accessTime"), from);
            } else if (to != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("accessTime"), to);
            }
            return null;
        };
    }
}
