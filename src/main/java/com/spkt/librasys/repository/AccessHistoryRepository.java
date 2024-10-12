package com.spkt.librasys.repository;

import com.spkt.librasys.entity.AccessHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
@Repository
public interface AccessHistoryRepository extends JpaRepository<AccessHistory, Long>, JpaSpecificationExecutor<AccessHistory> {
}
