package com.spkt.librasys.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "verification_tokens")
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    Long tokenId;

    @Column(name = "email", nullable = false)
    String email;

    @Column(name = "token", nullable = false)
    String token;

    @Column(name = "expiry_date", nullable = false)
    LocalDateTime expiryDate;
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    TokenType type;
    // Constructors, Getters, and Setters
    public enum TokenType {
        VERIFICATION,
        RESET_PASSWORD
    }
}