package com.katta.login;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LoginApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoginApplication.class, args);
	}

	// 1. Refresh Token
    //                   ↓
    //              2. Logout
    //                   ↓
    //           3. Roles / RBAC
    //                   ↓
    //          4. Email Verification
    //                   ↓
    //         5. Forgot Password
    //                   ↓
    //          6. Rate Limiting
    //                   ↓
    //          7. Security Hardening
    //                   ↓
    //          8. Integration Tests

}
