package com.spkt.librasys.repository;

import com.spkt.librasys.entity.Document;
import com.spkt.librasys.entity.DocumentType;
import com.spkt.librasys.entity.LoanPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoanPolicyRepository extends JpaRepository<LoanPolicy, Long> {

    /**
     * Tìm LoanPolicy dựa trên vai trò người dùng và loại tài liệu.
     * @param documentTypeId Loại tài liệu
     * @return Optional LoanPolicy
     */
    Optional<LoanPolicy> findByDocumentType(DocumentType documentTypeId);
}