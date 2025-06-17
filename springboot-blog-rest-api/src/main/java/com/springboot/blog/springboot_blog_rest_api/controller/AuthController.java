package com.springboot.blog.springboot_blog_rest_api.controller;

import com.springboot.blog.springboot_blog_rest_api.payload.LoginDto;
import com.springboot.blog.springboot_blog_rest_api.payload.RegisterDto;
import com.springboot.blog.springboot_blog_rest_api.repository.UserRepository;
import com.springboot.blog.springboot_blog_rest_api.security.JwtTokenProvider;
import com.springboot.blog.springboot_blog_rest_api.service.AuthService;
import com.springboot.blog.springboot_blog_rest_api.service.impl.EmailServiceImpl;
import com.springboot.blog.springboot_blog_rest_api.utils.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;
    private final EmailServiceImpl emailService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(AuthService authService,
                          UserRepository userRepository,
                          EmailServiceImpl emailService,
                          JwtTokenProvider jwtTokenProvider) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto, HttpServletResponse response) {
        String token = authService.login(loginDto);
        List<String> roles = jwtTokenProvider.getRoles(token);
        String username = jwtTokenProvider.getUsername(token);
        boolean isEnabled = userRepository.findByUsername(username).get().isEnabled();

        if (isEnabled) {
            CookieUtil.setAuthCookies(response, token, roles);
            
            // Trả về thông tin user trong response body để frontend biết đăng nhập thành công
            Map<String, Object> userInfo = Map.of(
                    "username", username,
                    "roles", roles,
                    "authenticated", true
            );
            
            return ResponseEntity.ok(userInfo);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        CookieUtil.clearAuthCookies(response);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        String verifyToken = authService.register(registerDto);

        String verifyUrl = "http://localhost:8080/api/auth/verify?token=" + verifyToken;
        String subject = "ACCOUNT CONFIRM";
        String content = "This link will expire within five minutes. Click on it to confirm your account: " + verifyUrl;

        emailService.sendMail(registerDto.getEmail(), subject, content);

        return new ResponseEntity<>("Registered successfully!!! Check your email to confirm your account.", HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        return ResponseEntity.ok(Map.of(
                "username", userDetails.getUsername()
        ));
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        String responseMessage = authService.verifyEmail(token);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }
}