package com.example.recover.entity;

import com.example.recover.utils.UserRole;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "user")
@Data
@EqualsAndHashCode(callSuper = true)  // Lombok 继承需加这个
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    private String realName;
    private String email;
    private String phone;
    @Enumerated(EnumType.STRING)
    private UserRole role;
    // 是否启用
    private Boolean enabled;


}
