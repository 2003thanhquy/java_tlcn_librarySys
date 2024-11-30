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
@Entity(name = "reading_sessions")
public class ReadingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id", nullable = false, unique = true)
    Long sessionId; // ID của phiên đọc

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user; // Người dùng

    @ManyToOne
    @JoinColumn(name = "document_id", nullable = false)
    Document document; // Tài liệu mà người dùng đang đọc

    @Column(name = "current_page", nullable = false)
    int currentPage; // Số trang hiện tại người dùng đang đọc

    @Column(name = "last_read_at")
    LocalDateTime lastReadAt; // Thời gian người dùng đọc lần cuối

    @PrePersist
    protected void onCreate() {
        this.lastReadAt = LocalDateTime.now(); // Gán thời gian khi phiên đọc được tạo
    }

    // Getters and Setters
}
