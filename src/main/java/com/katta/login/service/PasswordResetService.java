package com.katta.login.service;

import com.katta.login.entity.PasswordResetToken;
import com.katta.login.entity.User;
import com.katta.login.repository.PasswordResetTokenRepository;
import com.katta.login.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.security.SecureRandom;

import org.apache.commons.codec.digest.DigestUtils;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final SecureRandom secureRandom = new SecureRandom();

    public PasswordResetService(
            PasswordResetTokenRepository tokenRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a password reset token for the given email.
     *
     * If the email does not exist, this method simply returns.
     * This prevents account enumeration.
     */
    @Transactional
    public String createPasswordResetToken(String email) {

        Optional<User> optionalUser =
                userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();

        // Generate a cryptographically secure random token
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);

        String rawToken =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(bytes);

        // Store only the hash in the database
        String tokenHash =
                DigestUtils.sha256Hex(rawToken);

        PasswordResetToken resetToken =
                new PasswordResetToken();

        resetToken.setTokenHash(tokenHash);
        resetToken.setUser(user);
        resetToken.setExpiresAt(
                LocalDateTime.now().plusMinutes(15)
        );
        resetToken.setUsed(false);

        tokenRepository.save(resetToken);

        /*
         * IMPORTANT:
         *
         * The rawToken is NOT stored in the database.
         *
         * This raw token should be sent to the user's
         * email as part of the password reset link.
         *
         * Example:
         *
         * https://your-frontend.com/reset-password?token=<rawToken>
         */

        return rawToken;
    }

    /**
     * Resets the user's password using a valid reset token.
     */
    @Transactional
    public void resetPassword(
            String rawToken,
            String newPassword) {

        // Hash the token supplied by the user
        String tokenHash =
                DigestUtils.sha256Hex(rawToken);

        // Find the token in the database
        PasswordResetToken resetToken =
                tokenRepository.findByTokenHash(tokenHash)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid reset token"
                                )
                        );

        // Make sure the token hasn't already been used
        if (resetToken.isUsed()) {
            throw new RuntimeException(
                    "Reset token has already been used"
            );
        }

        // Make sure the token hasn't expired
        if (resetToken.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException(
                    "Reset token has expired"
            );
        }

        // Get the user associated with the token
        User user = resetToken.getUser();

        // Hash the new password before storing it
        user.setPassword(
                passwordEncoder.encode(newPassword)
        );

        userRepository.save(user);

        // Make the reset token single-use
        resetToken.setUsed(true);

        tokenRepository.save(resetToken);
    }
}