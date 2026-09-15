package com.katta.login.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.katta.login.dto.AuthResponse;
import com.katta.login.dto.ForgotPasswordRequest;
import com.katta.login.dto.LoginRequest;
import com.katta.login.dto.RefreshTokenRequest;
import com.katta.login.dto.ResetPasswordRequest;
import com.katta.login.dto.SignupRequest;
import com.katta.login.entity.RefreshToken;
import com.katta.login.entity.User;
import com.katta.login.service.AuthService;
import com.katta.login.service.EmailService;
import com.katta.login.service.JwtService;
import com.katta.login.service.PasswordResetService;
import com.katta.login.service.RefreshTokenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetService passwordResetService;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService, JwtService jwtService, PasswordResetService passwordResetService, EmailService emailService) {
        this.authService = authService;
        this.emailService = emailService;
        this.passwordResetService = passwordResetService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @Valid @RequestBody SignupRequest request) {

        User user = authService.signup(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "User registered successfully",
                        "userId", user.getId(),
                        "email", user.getEmail()
                ));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestBody RefreshTokenRequest request) {

        RefreshToken refreshToken =
                refreshTokenService.findByToken(
                        request.getRefreshToken()
                );

        refreshTokenService.revokeFamily(
                refreshToken.getFamilyId()
        );

        return ResponseEntity.noContent().build();
    }

        @PostMapping("/forgot-password")
        public ResponseEntity<Map<String, String>> forgotPassword(
                @Valid @RequestBody ForgotPasswordRequest request) {

        String resetToken =
                passwordResetService.createPasswordResetToken(
                        request.email()
                );

        if (resetToken != null) {
                emailService.sendPasswordResetEmail(
                        request.email(),
                        resetToken
                );
        }

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "If an account exists, a password reset link has been sent."
                )
        );
        }
    
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        passwordResetService.resetPassword(
                request.token(),
                request.newPassword()
        );

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        User user = authService.login(request);

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(user);

        String accessToken =
                jwtService.generateToken(
                        user.getEmail(),
                        refreshToken.getFamilyId()
                );

        return ResponseEntity.ok(
                new AuthResponse(
                        accessToken,
                        refreshToken.getToken()
                )
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @RequestBody RefreshTokenRequest request) {

        // RefreshToken refreshToken =
        //         refreshTokenService.findByToken(
        //                 request.getRefreshToken()
        //         );

        // if (refreshToken.isRevoked()) {
        //     throw new RuntimeException("Refresh token has been revoked");
        // }

        // if (refreshTokenService.isExpired(refreshToken)) {
        //     throw new RuntimeException("Refresh token has expired");
        // }

        // User user = refreshToken.getUser();

        // String accessToken =
        //         jwtService.generateToken(user.getEmail());

        // return ResponseEntity.ok(
        //         new AuthResponse(
        //                 accessToken,
        //                 refreshToken.getToken()
        //         )
        // );

        

        RefreshToken refreshToken =
            refreshTokenService.findByToken(
                    request.getRefreshToken()
            );

    // Reuse detection
    if (refreshToken.isRevoked()) {
        refreshTokenService.revokeFamily(
                refreshToken.getFamilyId()
        );

        throw new RuntimeException(
                "Refresh token reuse detected"
        );
    }

    // Expiration check
    if (refreshTokenService.isExpired(refreshToken)) {
        throw new RuntimeException(
                "Refresh token has expired"
        );
    }

    User user = refreshToken.getUser();

    // Rotate old token
    refreshTokenService.revokeToken(refreshToken.getToken());

    // Create new token in same family
    RefreshToken newRefreshToken =
            refreshTokenService.createRefreshToken(
                    user,
                    refreshToken.getFamilyId()
            );

    // Generate new access token
    String accessToken =
            jwtService.generateToken(user.getEmail(), newRefreshToken.getFamilyId());

    return ResponseEntity.ok(
            new AuthResponse(
                    accessToken,
                    newRefreshToken.getToken()
            )
    );
    }
}
