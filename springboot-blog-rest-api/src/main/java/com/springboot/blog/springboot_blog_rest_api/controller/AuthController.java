package com.springboot.blog.springboot_blog_rest_api.controller;

import com.springboot.blog.springboot_blog_rest_api.payload.LoginDto;
import com.springboot.blog.springboot_blog_rest_api.payload.RegisterDto;
import com.springboot.blog.springboot_blog_rest_api.repository.UserRepository;
import com.springboot.blog.springboot_blog_rest_api.security.JwtTokenProvider;
import com.springboot.blog.springboot_blog_rest_api.service.AuthService;
import com.springboot.blog.springboot_blog_rest_api.service.impl.EmailServiceImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Void> login(@RequestBody LoginDto loginDto) {
        String token = authService.login(loginDto);

        // Sử dụng ResponseCookie từ Spring Framework
        ResponseCookie jwtCookie = ResponseCookie.from("jwtToken", token)
                .httpOnly(true)
                .secure(true)  // Chỉ sử dụng nếu server chạy HTTPS
                .path("/")
                .maxAge(24 * 60 * 60 * 7)  // Cookie tồn tại trong 7 ngày
                .sameSite("Strict")  // Bảo vệ chống lại CSRF
                .build();

        List<String> roles = jwtTokenProvider.getRoles(token);
        String rolesString = String.join(",", roles);

        ResponseCookie rolesCookie = ResponseCookie.from("roles", rolesString)
                .httpOnly(false)
                .secure(true)
                .path("/")
                .maxAge(24 * 60 * 60 * 7)
                .sameSite("Strict")
                .build();

        String username = jwtTokenProvider.getUsername(token);
        boolean isEnabled = userRepository.findByUsername(username).get().isEnabled();

        if (isEnabled) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, rolesCookie.toString())
                    .build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // Tạo cookie jwtToken hết hạn để xóa
        ResponseCookie jwtCookie = ResponseCookie.from("jwtToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)  // Đặt thời gian hết hạn về 0 để xóa cookie
                .sameSite("Strict")
                .build();

        // Tạo cookie roles hết hạn để xóa
        ResponseCookie rolesCookie = ResponseCookie.from("roles", "")
                .httpOnly(false)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, rolesCookie.toString())
                .build();
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

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        String responseMessage = authService.verifyEmail(token);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }
}
