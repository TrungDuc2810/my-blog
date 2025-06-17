package com.springboot.blog.springboot_blog_rest_api.config;

import com.springboot.blog.springboot_blog_rest_api.entity.User;
import com.springboot.blog.springboot_blog_rest_api.repository.RoleRepository;
import com.springboot.blog.springboot_blog_rest_api.repository.UserRepository;
import com.springboot.blog.springboot_blog_rest_api.security.CookieDebugFilter;
import com.springboot.blog.springboot_blog_rest_api.security.CustomOAuth2AuthorizationRequestResolver;
import com.springboot.blog.springboot_blog_rest_api.security.JwtAuthenticationFilter;
import com.springboot.blog.springboot_blog_rest_api.security.JwtTokenProvider;
import com.springboot.blog.springboot_blog_rest_api.service.impl.CustomOAuth2UserService;
import com.springboot.blog.springboot_blog_rest_api.service.impl.OAuth2UserServiceHelper;
import com.springboot.blog.springboot_blog_rest_api.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;
import java.util.Optional;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final HttpCookieOAuth2AuthorizationRequest httpCookieOAuth2AuthorizationRequest;
    private final OAuth2UserServiceHelper oAuth2UserServiceHelper;
    @Value("${frontend.url}")
    private String frontendUrl;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService,
                          HttpCookieOAuth2AuthorizationRequest httpCookieOAuth2AuthorizationRequest,
                          JwtTokenProvider jwtTokenProvider,
                          UserDetailsService userDetailsService,
                          OAuth2UserServiceHelper oAuth2UserServiceHelper) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.httpCookieOAuth2AuthorizationRequest = httpCookieOAuth2AuthorizationRequest;
        this.oAuth2UserServiceHelper = oAuth2UserServiceHelper;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   UserRepository userRepository,
                                                   RoleRepository roleRepository,
                                                   CookieDebugFilter cookieDebugFilter) throws Exception {
        http.cors(cors -> {}).csrf(csrf -> csrf.disable());
        http.addFilterBefore(cookieDebugFilter, UsernamePasswordAuthenticationFilter.class);

        configureAuthorizeRequests(http);
        configureOAuth2Login(http, userRepository, roleRepository);
        configureSessionAndFilters(http);

        return http.build();
    }

    private void configureAuthorizeRequests(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/oauth2/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/vnpay/**").permitAll()
                .requestMatchers(HttpMethod.GET).permitAll()
                .anyRequest().authenticated());
    }

    private void configureOAuth2Login(HttpSecurity http,
                                      UserRepository userRepository,
                                      RoleRepository roleRepository) throws Exception {
        http.oauth2Login(oauth -> oauth
                .authorizationEndpoint(endpoint -> endpoint
                        .authorizationRequestResolver(customAuthorizationRequestResolver(http.getSharedObject(ClientRegistrationRepository.class)))
                        .authorizationRequestRepository(cookieAuthorizationRequest()))
                .userInfoEndpoint(info -> info.userService(customOAuth2UserService))
                .successHandler((request, response, authentication) -> {
                    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
                    String email = oAuth2User.getAttribute("email");
                    String name = oAuth2User.getAttribute("name");

                    Optional<User> optionalUser = userRepository.findByEmail(email);
                    if (optionalUser.isEmpty()) {
                        oAuth2UserServiceHelper.registerOAuthUser(email, name, roleRepository, userRepository);
                    }

                    var userDetails = userDetailsService.loadUserByUsername(email);
                    var authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    String jwt = jwtTokenProvider.generateToken(authToken);

                    List<String> roles = userDetails.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .toList();

                    CookieUtil.setAuthCookies(response, jwt, roles);

                    response.sendRedirect(frontendUrl);
                }));
    }

    private void configureSessionAndFilters(HttpSecurity http) throws Exception {
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public OAuth2AuthorizationRequestResolver customAuthorizationRequestResolver(
            ClientRegistrationRepository clientRegistrationRepository) {
        return new CustomOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization");
    }


    @Bean
    public HttpCookieOAuth2AuthorizationRequest cookieAuthorizationRequest() {
        return new HttpCookieOAuth2AuthorizationRequest();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
