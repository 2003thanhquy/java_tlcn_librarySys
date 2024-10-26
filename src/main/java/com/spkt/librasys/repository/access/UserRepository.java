package com.spkt.librasys.repository.access;

import com.spkt.librasys.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);
    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
    //dashboard
    @Query("SELECT COUNT(u) FROM users_001 u WHERE u.registrationDate BETWEEN :start AND :end")
    Long countNewUsersInCurrentMonth(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT COUNT(a) FROM access_histories_001 a WHERE a.accessTime BETWEEN :start AND :end")
    Long countActiveUsersInCurrentMonth(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
