package com.katta.login.dto;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}
