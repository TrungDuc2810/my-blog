package com.springboot.blog.springboot_blog_rest_api.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.util.SerializationUtils;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

public class CookieUtil {
    public static Optional<Cookie> getCookie(HttpServletRequest request,
                                             String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return Optional.of(cookie);
                }
            }
        }
        return Optional.empty();
    }

    public static void addCookie(HttpServletResponse response,
                                 String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        cookie.setSecure(true);
        response.addCookie(cookie);
    }

    public static void deleteCookie(HttpServletRequest request,
                                    HttpServletResponse response,
                                    String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }

    public static void setAuthCookies(HttpServletResponse response, String jwt, List<String> roles) {
        // Log trước khi thiết lập cookie
        System.out.println("Setting auth cookies - JWT length: " + jwt.length() + ", Roles: " + roles);
        
        ResponseCookie.ResponseCookieBuilder jwtBuilder = ResponseCookie.from("jwtToken", jwt)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(7 * 24 * 60 * 60);
        
        ResponseCookie.ResponseCookieBuilder rolesBuilder = ResponseCookie.from("roles", String.join(",", roles))
                .httpOnly(false) // Set to false so frontend can read roles
                .secure(true)
                .path("/")
                .maxAge(7 * 24 * 60 * 60);
        
        // Thêm SameSite=None cho cross-origin
        jwtBuilder.sameSite("None");
        rolesBuilder.sameSite("None");
        
        ResponseCookie jwtCookie = jwtBuilder.build();
        ResponseCookie rolesCookie = rolesBuilder.build();
        
        // Thêm cookies vào response
        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, rolesCookie.toString());
        
        // Log sau khi thiết lập cookie
        System.out.println("JWT Cookie: " + jwtCookie);
        System.out.println("Roles Cookie: " + rolesCookie);
    }

    public static void clearAuthCookies(HttpServletResponse response) {
        System.out.println("Clearing auth cookies");
        
        ResponseCookie jwtCookie = ResponseCookie.from("jwtToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("None")
                .build();
        
        ResponseCookie rolesCookie = ResponseCookie.from("roles", "")
                .httpOnly(false)  // Match the setting in setAuthCookies
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("None")
                .build();
        
        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, rolesCookie.toString());
        
        System.out.println("Cleared JWT Cookie: " + jwtCookie);
        System.out.println("Cleared Roles Cookie: " + rolesCookie);
    }

    // Ma hoa
    public static String serialize(Object obj) {
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(obj));
    }

    // Giai ma
    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.getValue())));
    }
}