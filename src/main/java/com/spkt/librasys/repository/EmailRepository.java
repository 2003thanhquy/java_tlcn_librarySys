package com.spkt.librasys.repository;

import com.spkt.librasys.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<Email, String> {
}
