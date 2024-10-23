package com.spkt.librasys.repository.specification;

import com.spkt.librasys.entity.LoanTransaction;
import org.springframework.data.jpa.domain.Specification;

public class LoanTransactionSpecification {

    public static Specification<LoanTransaction> hasStatus(LoanTransaction.Status status) {
        return (root, query, criteriaBuilder) ->
                status == null ? null : criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<LoanTransaction> hasUsername(String username) {
        return (root, query, criteriaBuilder) ->
                username == null ? null : criteriaBuilder.like(root.get("user").get("username"), "%" + username + "%");
    }

    public static Specification<LoanTransaction> hasDocumentName(String documentName) {
        return (root, query, criteriaBuilder) ->
                documentName == null ? null : criteriaBuilder.like(root.get("document").get("documentName"), "%" + documentName + "%");
    }
}
