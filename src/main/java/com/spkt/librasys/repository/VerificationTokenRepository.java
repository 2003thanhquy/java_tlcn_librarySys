package com.spkt.librasys.repository;

import com.spkt.librasys.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByEmailAndToken(String email,String token);
}
