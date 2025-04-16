package com.springboot.blog.springboot_blog_rest_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.springboot.blog.springboot_blog_rest_api.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String username, String email);
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}

