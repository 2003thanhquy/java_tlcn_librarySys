package com.spkt.librasys.entity;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

import lombok.*;
import lombok.experimental.FieldDefaults;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name="roles_001")
public class Role {
    @Id
    String name;

    String description;

    @ManyToMany(mappedBy = "roles")
    @JsonBackReference
    Set<User> users;
//    @ManyToMany
//    Set<Permission> permissions;
}
