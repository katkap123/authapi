package com.katta.login.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;

@Service
public class EmailService {

    private final Resend resend;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public EmailService(
            @Value("${resend.api-key}") String apiKey) {

        this.resend = new Resend(apiKey);
    }

    public void sendPasswordResetEmail(
            String recipientEmail,
            String resetToken) {

        String resetLink =
                frontendUrl
                        + "/reset-password?token="
                        + resetToken;

        CreateEmailOptions params =
                CreateEmailOptions.builder()
                        .from("Auth API <onboarding@resend.dev>")
                        .to(recipientEmail)
                        .subject("Reset your password")
                        .html("""
                                <h2>Password Reset</h2>

                                <p>Hello,</p>

                                <p>
                                    We received a request to reset your password.
                                </p>

                                <p>
                                    Click the button below to reset your password:
                                </p>

                                <p>
                                    <a href="%s"
                                       style="
                                           display:inline-block;
                                           padding:12px 20px;
                                           background:#2563eb;
                                           color:white;
                                           text-decoration:none;
                                           border-radius:6px;
                                       ">
                                        Reset Password
                                    </a>
                                </p>

                                <p>
                                    This link will expire soon and can only be used once.
                                </p>

                                <p>
                                    If you did not request a password reset,
                                    you can safely ignore this email.
                                </p>

                                <p>
                                    Thanks,<br>
                                    Auth API
                                </p>
                                """.formatted(resetLink))
                        .build();

        try {
            resend.emails().send(params);
        } catch (ResendException e) {
            throw new RuntimeException(
                    "Failed to send password reset email",
                    e
            );
        }
    }
}