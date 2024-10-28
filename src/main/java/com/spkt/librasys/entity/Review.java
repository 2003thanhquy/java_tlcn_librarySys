package com.spkt.librasys.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "reviews_001")
@Table(
    name = "reviews_001",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "document_id"})
    }
)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    @ToString.Exclude
    Document document;

    @Column(name = "rating", nullable = false)
    int rating; // Rating from 1 to 5

    @Column(name = "comment", nullable = false, columnDefinition = "TEXT")
    String comment;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    Status status = Status.APPROVED;

    // Optional: For reply functionality (nested comments)
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "parent_review_id")
//    @ToString.Exclude
//    Review parentReview;
//
//    @OneToMany(mappedBy = "parentReview", cascade = CascadeType.ALL, orphanRemoval = true)
//    List<Review> replies;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public enum Status {
        PENDING,    // Awaiting approval
        APPROVED,   // Visible to others
        REJECTED    // Not approved
    }
}
