package com.spkt.librasys.repository;

import com.spkt.librasys.entity.Document;
import com.spkt.librasys.entity.ReadingSession;
import com.spkt.librasys.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadingSessionRepository extends JpaRepository<ReadingSession, Long> {
    Optional<ReadingSession> findByUserAndDocument(User user, Document document); // Tìm phiên đọc theo người dùng và tài liệu
    List<ReadingSession> findByUser(User user); // Lấy tất cả phiên đọc của một người dùng
}
