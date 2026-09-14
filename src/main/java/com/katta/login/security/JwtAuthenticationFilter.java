package com.katta.login.security;

import java.io.IOException;
import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.katta.login.service.CustomUserDetailsService;
import com.katta.login.service.JwtService;
import com.katta.login.service.RefreshTokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService,
            RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("===== JWT FILTER START =====");

        String authHeader =
                request.getHeader("Authorization");

        System.out.println(
                "Authorization header exists: "
                + (authHeader != null)
        );

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            System.out.println("No Bearer token");

            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {

            System.out.println("Validating JWT...");

            if (!jwtService.isTokenValid(token)) {

                System.out.println("JWT INVALID");

                filterChain.doFilter(request, response);
                return;
            }

            System.out.println("JWT VALID");

            String email =
                    jwtService.extractEmail(token);

            UUID familyId =
                    jwtService.extractFamilyId(token);

            System.out.println(
                    "Email from JWT: " + email
            );

            System.out.println(
                    "Family ID from JWT: " + familyId
            );

            // IMPORTANT:
            // Check whether the session/family is still active
            if (!refreshTokenService
                    .isFamilyActive(familyId)) {

                System.out.println(
                        "SESSION REVOKED"
                );

                SecurityContextHolder
                        .clearContext();

                filterChain.doFilter(
                        request,
                        response
                );

                return;
            }

            UserDetails userDetails =
                    userDetailsService
                            .loadUserByUsername(email);

            System.out.println(
                    "User loaded: "
                    + userDetails.getUsername()
            );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

            System.out.println(
                    "Authentication set successfully"
            );

        } catch (Exception e) {

            System.out.println("===== JWT ERROR =====");
            e.printStackTrace();

            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
    }
