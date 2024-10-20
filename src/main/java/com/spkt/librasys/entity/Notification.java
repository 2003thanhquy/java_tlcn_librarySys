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
@Entity(name = "notification_001")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    User user;

    @Column(nullable = false)
    String title;

    @Column(nullable = false)
    String content;

    @Column(nullable = false)
    LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    NotificationStatus status; // UNREAD, READ

//    @Enumerated(EnumType.STRING) // Optional, nếu dùng enum cho groupName
//    @Column(name = "group_name")
//    GroupName groupName; // Nhóm người dùng (VD: "ADMIN", "USER")

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    public enum NotificationStatus {
        UNREAD, READ
    }

}
