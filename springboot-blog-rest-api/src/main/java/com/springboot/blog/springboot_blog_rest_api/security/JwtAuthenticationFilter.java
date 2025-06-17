package com.springboot.blog.springboot_blog_rest_api.security;

import com.springboot.blog.springboot_blog_rest_api.utils.CookieUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

import org.springframework.util.StringUtils;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private JwtTokenProvider jwtTokenProvider;
    private UserDetailsService userDetailsService;

    @Autowired
    public void setJwtTokenProvider(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        
        // Get JWT from cookie
        String token = null;
        Optional<Cookie> jwtCookie = CookieUtil.getCookie(request, "jwtToken");
        
        if (jwtCookie.isPresent()) {
            token = jwtCookie.get().getValue();
            System.out.println("Found JWT token in cookie: " + (token != null ? token.substring(0, Math.min(10, token.length())) + "..." : "null"));
        } else {
            System.out.println("No JWT cookie found in request to: " + request.getRequestURI());
        }
        
        // Validate token and set authentication
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            String username = jwtTokenProvider.getUsername(token);
            System.out.println("Valid token for user: " + username);
            
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            System.out.println("Set authentication for user: " + username);
        }
        
        filterChain.doFilter(request, response);
    }
}