package com.example.recover.repository;

import com.example.recover.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    // 自动生成的 SQL 类似于：SELECT count(id) FROM user WHERE username = ? > 0
    boolean existsByUsername(String username);
}
