package com.springboot.blog.springboot_blog_rest_api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CookieDebugFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Debug request cookies
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            System.out.println("Request cookies for " + request.getRequestURI() + ":");
            for (Cookie cookie : cookies) {
                System.out.println("  " + cookie.getName() + ": " + cookie.getValue());
            }
        } else {
            System.out.println("No cookies in request to: " + request.getRequestURI());
        }
        
        // Capture response after processing
        CookieTrackingResponseWrapper responseWrapper = new CookieTrackingResponseWrapper(response);
        filterChain.doFilter(request, responseWrapper);
        
        // Log response cookies if they were set
        List<String> setCookieHeaders = responseWrapper.getCapturedSetCookieHeaders();
        if (!setCookieHeaders.isEmpty()) {
            System.out.println("Response cookies from " + request.getRequestURI() + ":");
            for (String header : setCookieHeaders) {
                System.out.println("  " + header);
            }
        }
        
        responseWrapper.copyBodyToResponse();
    }
    
    // Helper class to track cookies
    private static class CookieTrackingResponseWrapper extends HttpServletResponseWrapper {
        private final List<String> capturedSetCookieHeaders = new ArrayList<>();
        private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        private final PrintWriter writer = new PrintWriter(baos);
        
        public CookieTrackingResponseWrapper(HttpServletResponse response) {
            super(response);
        }
        
        @Override
        public void addHeader(String name, String value) {
            if ("Set-Cookie".equalsIgnoreCase(name)) {
                capturedSetCookieHeaders.add(value);
            }
            super.addHeader(name, value);
        }
        
        public List<String> getCapturedSetCookieHeaders() {
            return capturedSetCookieHeaders;
        }
        
        @Override
        public PrintWriter getWriter() {
            return writer;
        }
        
        public void copyBodyToResponse() throws IOException {
            writer.flush();
            if (baos.size() > 0) {
                getResponse().getOutputStream().write(baos.toByteArray());
            }
        }
    }
}