package com.katta.login.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class InternalApiKeyFilter extends OncePerRequestFilter {

    private static final String HEADER_NAME = "X-Internal-Api-Key";

    private final String internalApiKey;

    public InternalApiKeyFilter(
            @Value("${internal.api.key}") String internalApiKey) {
        this.internalApiKey = internalApiKey;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/internal/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("===== INTERNAL API KEY FILTER =====");
        System.out.println("Request URI: " + request.getRequestURI());

        String providedApiKey = request.getHeader(HEADER_NAME);

        System.out.println("API key header present: " + (providedApiKey != null));
        System.out.println(
            "API key matches: " +
            (providedApiKey != null && internalApiKey.equals(providedApiKey))
        );

        if (providedApiKey == null || !internalApiKey.equals(providedApiKey)) {
            System.out.println("Internal API key rejected");

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write(
                "{\"message\":\"Invalid internal API key\"}"
            );
            return;
        }

        System.out.println("Internal API key accepted");

        filterChain.doFilter(request, response);
    }
}