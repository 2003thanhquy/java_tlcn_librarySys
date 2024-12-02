package com.spkt.librasys.repository;

import com.spkt.librasys.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);
    VerificationToken findByEmailAndToken(String email,String token);
    Optional<VerificationToken> findByEmailAndTokenAndType(String email, String token, VerificationToken.TokenType type);
}
