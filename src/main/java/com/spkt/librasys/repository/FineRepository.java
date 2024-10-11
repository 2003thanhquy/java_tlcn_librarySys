package com.spkt.librasys.repository;

import com.spkt.librasys.entity.Fine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FineRepository extends JpaRepository<Fine, String> {
    List<Fine> findByUserUserId(String userId);
}
