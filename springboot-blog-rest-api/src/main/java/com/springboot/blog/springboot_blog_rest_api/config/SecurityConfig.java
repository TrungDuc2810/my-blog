package com.springboot.blog.springboot_blog_rest_api.config;

import com.springboot.blog.springboot_blog_rest_api.security.JwtAuthenticationFilter;
import com.springboot.blog.springboot_blog_rest_api.security.JwtTokenProvider;
import com.springboot.blog.springboot_blog_rest_api.service.impl.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final HttpCookieOAuth2AuthorizationRequest httpCookieOAuth2AuthorizationRequest;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService,
                          HttpCookieOAuth2AuthorizationRequest httpCookieOAuth2AuthorizationRequest,
                          JwtTokenProvider jwtTokenProvider,
                          UserDetailsService userDetailsService) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.httpCookieOAuth2AuthorizationRequest = httpCookieOAuth2AuthorizationRequest;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/oauth/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/vnpay/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(oauth -> oauth
                        .authorizationEndpoint(endpoint -> endpoint
                                .authorizationRequestRepository(cookieAuthorizationRequest())
                        )
                        .userInfoEndpoint(info -> info.userService(customOAuth2UserService))
                        .successHandler((request, response, authentication) -> {
                            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
                            String usernameOrEmail = oAuth2User.getAttribute("email");

                            var userDetails = userDetailsService.loadUserByUsername(usernameOrEmail);
                            var authToken = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());

                            String jwt = jwtTokenProvider.generateToken(authToken);

                            ResponseCookie jwtCookie = ResponseCookie.from("jwtToken", jwt)
                                    .httpOnly(true)
                                    .secure(true)
                                    .path("/")
                                    .maxAge(7 * 24 * 60 * 60)
                                    .sameSite("Strict")
                                    .build();

                            List<String> roles = userDetails.getAuthorities().stream()
                                    .map(GrantedAuthority::getAuthority).toList();

                            ResponseCookie rolesCookie = ResponseCookie.from("roles", String.join(",", roles))
                                    .httpOnly(false)
                                    .secure(true)
                                    .path("/")
                                    .maxAge(7 * 24 * 60 * 60)
                                    .sameSite("Strict")
                                    .build();

                            response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
                            response.addHeader(HttpHeaders.SET_COOKIE, rolesCookie.toString());
                            response.sendRedirect("https://chatgpt.com/");
                        }))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        // Thêm JwtAuthenticationFilter trước UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public HttpCookieOAuth2AuthorizationRequest cookieAuthorizationRequest() {
        return new HttpCookieOAuth2AuthorizationRequest();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
