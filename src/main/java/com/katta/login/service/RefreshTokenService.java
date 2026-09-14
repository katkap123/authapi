package com.katta.login.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.katta.login.entity.RefreshToken;
import com.katta.login.entity.User;
import com.katta.login.repository.RefreshTokenRepository;
import com.katta.login.repository.UserRepository;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    private final long refreshTokenExpiration;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            UserRepository userRepository,
            @Value("${jwt.refresh-expiration}")
            long refreshTokenExpiration) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    @Transactional
    public RefreshToken createRefreshToken(
            User user,
            UUID familyId) {

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setFamilyId(familyId);

        refreshToken.setExpiresAt(
            LocalDateTime.now()
                .plusSeconds(refreshTokenExpiration)
        );

        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    public boolean isFamilyActive(UUID familyId) {

        return refreshTokenRepository
                .existsByFamilyIdAndRevokedFalse(familyId);
    }

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        return createRefreshToken(
                user,
                UUID.randomUUID()
        );
    }

    public RefreshToken findByToken(String token) {

        return refreshTokenRepository
                .findByToken(token)
                .orElseThrow(() ->
                        new RuntimeException("Invalid refresh token"));
    }

    public boolean isExpired(RefreshToken refreshToken) {

        return refreshToken.getExpiresAt()
                .isBefore(LocalDateTime.now());
    }

    @Transactional
    public void revokeFamily(UUID familyId) {

        refreshTokenRepository.revokeAllByFamilyId(familyId);
    }
    
    @Transactional
    public void revokeToken(String token) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(token)
                .orElseThrow(() ->
                        new RuntimeException("Invalid refresh token"));

        refreshToken.setRevoked(true);

        refreshTokenRepository.save(refreshToken);
    }
}