package com.spkt.librasys.repository;

import com.spkt.librasys.entity.Rack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RackRepository extends JpaRepository<Rack, Long> {
    Optional<Rack> findByRackNumberAndShelf_ShelfId(String rackNumber, Long shelfId);
}
