package com.springboot.blog.springboot_blog_rest_api.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

public class CustomOAuth2AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final OAuth2AuthorizationRequestResolver defaultResolver;

    public CustomOAuth2AuthorizationRequestResolver(ClientRegistrationRepository repo, String authorizationRequestBaseUri) {
        this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(repo, authorizationRequestBaseUri);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest requestToGoogle = defaultResolver.resolve(request);
        return customize(requestToGoogle);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest requestToGoogle = defaultResolver.resolve(request, clientRegistrationId);
        return customize(requestToGoogle);
    }

    private OAuth2AuthorizationRequest customize(OAuth2AuthorizationRequest originalRequest) {
        if (originalRequest == null) return null;

        return OAuth2AuthorizationRequest.from(originalRequest)
                .additionalParameters(params -> params.put("prompt", "select_account"))
                .build();
    }
}