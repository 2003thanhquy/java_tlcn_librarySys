package com.spkt.librasys.repository;

import com.spkt.librasys.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByName(String name);
    boolean existsByNameAndUsersUserId(String roleName, String userId);
}