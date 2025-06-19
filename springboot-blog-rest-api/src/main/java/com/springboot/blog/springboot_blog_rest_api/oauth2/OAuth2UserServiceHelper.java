package com.springboot.blog.springboot_blog_rest_api.oauth2;

import com.springboot.blog.springboot_blog_rest_api.entity.Role;
import com.springboot.blog.springboot_blog_rest_api.entity.User;
import com.springboot.blog.springboot_blog_rest_api.repository.RoleRepository;
import com.springboot.blog.springboot_blog_rest_api.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
public class OAuth2UserServiceHelper {

    private final PasswordEncoder passwordEncoder;

    public OAuth2UserServiceHelper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void registerOAuthUser(String email, String name, 
                                 RoleRepository roleRepository, 
                                 UserRepository userRepository) {
        // Tạo username từ email
        String username = email.substring(0, email.indexOf("@"));
        
        // Tạo một user mới
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setUsername(username);
        
        // Tạo mật khẩu ngẫu nhiên an toàn
        String randomPassword = UUID.randomUUID().toString();
        user.setPassword(passwordEncoder.encode(randomPassword));
        
        // Thiết lập người dùng đã được kích hoạt
        user.setEnabled(true);
        
        // Gán vai trò ROLE_USER
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("ROLE_USER").orElseThrow(() -> 
                new RuntimeException("Không tìm thấy vai trò ROLE_USER"));
        roles.add(userRole);
        user.setRoles(roles);
        
        // Lưu người dùng vào cơ sở dữ liệu
        userRepository.save(user);
    }
}