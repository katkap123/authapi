package com.katta.login.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(
            String recipientEmail,
            String resetToken) {

        String resetLink =
                frontendUrl + "/reset-password?token=" + resetToken;

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(recipientEmail);
        message.setSubject("Reset your password");

        message.setText(
                """
                Hello,

                We received a request to reset your password.

                Click the link below to reset your password:

                %s

                This link will expire soon and can only be used once.

                If you did not request a password reset, you can safely ignore this email.

                Thanks,
                Auth API
                """.formatted(resetLink)
        );

        mailSender.send(message);
    }
}
