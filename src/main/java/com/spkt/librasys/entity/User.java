package com.spkt.librasys.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "users_001")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", nullable = false, unique = true)
    String userId;

    @Column(name = "username", nullable = false, unique = true)
    String username;

    @Column(name = "password", nullable = false)
    String password;

    @Column(name = "first_name")//, nullable = false)
    String firstName;

    @Column(name = "last_name")//, nullable = false)
    String lastName;

    @Column(name = "dob")
    LocalDate dob;  // Ngày sinh

    @Column(name = "phone_number")
    String phoneNumber;

    @Column(name = "address")
    String address;

    @Column(name = "registration_date")
    //@Builder.Default
    LocalDate registrationDate = LocalDate.now(); // Giá trị mặc định là ngày hiện tại

    @Column(name = "expiration_date")
    LocalDate expirationDate;

    @Column(name = "current_borrowed_count", nullable = false)
    @Builder.Default
    int currentBorrowedCount = 0; // Giá trị mặc định là 0

    @Column(name = "max_borrow_limit", nullable = false)
    @Builder.Default
    int maxBorrowLimit = 5; // Giá trị mặc định là 5
    @Column(name = "locked_at")
    LocalDateTime lockedAt; // Ngày tài khoản bị khóa

    @Column(name = "deactivated_at")
    LocalDateTime deactivatedAt; // Ngày tài khoản bị vô hiệu hóa

    @Column(name = "reactivated_at")
    LocalDateTime reactivatedAt; // Ngày tài khoản được kích hoạt lại

    @Column(name = "deactivation_reason")
    String deactivationReason; // Lý do vô hiệu hóa tài khoản

    @Column(name = "lock_reason")
    String lockReason; // Lý do tài khoản bị khóa

    @Column(name = "lock_count", nullable = false)
    int lockCount = 0; // Số lần tài khoản bị khóa

    // Quan hệ Many-to-One với Department
    @ManyToOne
    @JoinColumn(name = "department_id")
    Department department;


    @Enumerated(EnumType.STRING)
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    Status isActive = Status.PENDING;
    // Một người dùng có nhiều lịch sử truy cập
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    List<AccessHistory> accessHistories;

    // Mối quan hệ nhiều-nhiều với các vai trò
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles", // Tên bảng trung gian
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_name")
    )
    @JsonManagedReference
    Set<Role> roles;

    // Một người dùng có nhiều khoản phạt
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Fine> fines;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Notification> notifications;
    public enum Status {
        PENDING,     // Đăng ký nhưng chưa được xác minh
        ACTIVE,       // Tài khoản đang hoạt động
        DEACTIVATED,  // Tài khoản đã bị vô hiệu hóa
        LOCKED,       // Tài khoản bị khóa (do vi phạm, bảo mật, hoặc các lý do khác)
        DELETED       // Tài khoản đã bị xóa (đánh dấu là không còn hoạt động)
    }


    @PrePersist
    protected void onCreate() {
        // Đảm bảo rằng các trường mặc định được gán giá trị khi entity được lưu lần đầu
        if (this.currentBorrowedCount == 0) {
            this.currentBorrowedCount = 0;
        }
        if (this.maxBorrowLimit == 0) {
            this.maxBorrowLimit = 5;
        }
        if (this.isActive == null) {
            this.isActive = Status.ACTIVE;
        }
    }
}
