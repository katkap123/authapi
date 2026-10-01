package com.katta.login.controllers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.katta.login.dto.UserSummaryResponse;
import com.katta.login.service.CustomUserDetailsService;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private final CustomUserDetailsService customUserDetailsService;
    public UserController(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }
    @GetMapping("/me")
    public Map<String, Object> getCurrentUser(
            Authentication authentication) {
        System.out.println("invokes");
        return Map.of(
                "email", authentication.getName(),
                "message", "You are authenticated"
        );
    }

    @PostMapping("/summaries")
    public ResponseEntity<List<UserSummaryResponse>> getUserSummaries(
            @RequestBody List<UUID> userIds) {

        List<UserSummaryResponse> users;
        users = this.customUserDetailsService.getUserSummaries(userIds);

        return ResponseEntity.ok(users);
    }
}
