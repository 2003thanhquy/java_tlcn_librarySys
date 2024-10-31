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
@Entity(name = "access_histories_001")
public class AccessHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "access_id")
    Long accessId;

    @Column(name = "access_time", nullable = false)
    LocalDateTime accessTime;  // Thời gian truy cập

    @Column(name = "activity", nullable = false)
    @Enumerated(EnumType.STRING)
    Activity activity;  // Hoạt động thực hiện (VD: Downloaded, Read Online)

    // Mối quan hệ nhiều-một với User (người truy cập)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    User user;

    // Mối quan hệ nhiều-một với Document (tài liệu được truy cập)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    Document document;
    public enum Activity {
        DOWNLOADED,
        READ_ONLINE,
        UPLOADED,
        VIEWED,
        DOC_UNAVAILABLE,
        // Thêm các hoạt động khác nếu cần
    }

}
