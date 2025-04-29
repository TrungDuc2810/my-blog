package com.springboot.blog.springboot_blog_rest_api.service.impl;

import com.springboot.blog.springboot_blog_rest_api.entity.Role;
import com.springboot.blog.springboot_blog_rest_api.entity.User;
import com.springboot.blog.springboot_blog_rest_api.repository.RoleRepository;
import com.springboot.blog.springboot_blog_rest_api.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class OAuth2UserServiceHelper {
    private PasswordEncoder passwordEncoder;

    public OAuth2UserServiceHelper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerOAuthUser(String email, String name,
                                  RoleRepository roleRepository,
                                  UserRepository userRepository) {
        Role role = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("ROLE_USER không tồn tại"));

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setUsername(email);
        newUser.setName(name);
        newUser.setPassword(passwordEncoder.encode("oauth2user"));
        newUser.setEnabled(true);
        newUser.setRoles(new HashSet<>(List.of(role)));

        return userRepository.save(newUser);
    }
}
