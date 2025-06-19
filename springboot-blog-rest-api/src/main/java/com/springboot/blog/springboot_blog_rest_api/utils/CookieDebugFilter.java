package com.springboot.blog.springboot_blog_rest_api.utils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class CookieDebugFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(CookieDebugFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        // Log yêu cầu URL
        logger.debug("Request URL: {}", request.getRequestURL());
        
        // Log cookies đến
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            logger.debug("Cookies in request:");
            for (Cookie cookie : cookies) {
                logger.debug("  {} = {}, HttpOnly: {}, Secure: {}, MaxAge: {}, Path: {}", 
                        cookie.getName(), 
                        cookie.getValue().substring(0, Math.min(10, cookie.getValue().length())) + "...", 
                        cookie.isHttpOnly(),
                        cookie.getSecure(),
                        cookie.getMaxAge(),
                        cookie.getPath());
            }
        } else {
            logger.debug("No cookies in request");
        }
        
        filterChain.doFilter(request, response);
    }
}