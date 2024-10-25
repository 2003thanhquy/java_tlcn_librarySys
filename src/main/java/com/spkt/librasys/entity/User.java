package com.spkt.librasys.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    Status is_active = Status.ACTIVE;
    // Một người dùng có nhiều lịch sử truy cập
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<AccessHistory> accessHistories;

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
        ACTIVE,DELETE,BLOCK,
    }
}
