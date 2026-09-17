package com.katta.login.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.katta.login.dto.LoginRequest;
import com.katta.login.dto.SignupRequest;
import com.katta.login.entity.Role;
import com.katta.login.entity.User;
import com.katta.login.exception.EmailAlreadyExistsException;
import com.katta.login.repository.RoleRepository;
import com.katta.login.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public User signup(SignupRequest request) {

        String email = request.email()
                .toLowerCase()
                .trim();

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(
                    "Email is already registered"
            );
        }


        Role userRole = roleRepository.findByName("STUDENT")
                .orElseThrow(() ->
                        new RuntimeException("STUDENT role not found"));

        User user = new User();

        user.setEmail(email);

        user.setPassword(
                passwordEncoder.encode(request.password())
        );

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());

        user.setEnabled(true);
        user.setEmailVerified(false);

        user.getRoles().add(userRole);

        return userRepository.save(user);
    }

    public User login(LoginRequest request) {

        String email = request.email()
                .toLowerCase()
                .trim();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password"));

        if (!user.isEnabled()) {
            throw new RuntimeException("User account is disabled");
        }

        boolean passwordMatches =
                passwordEncoder.matches(
                        request.password(),
                        user.getPassword()
                );

        if (!passwordMatches) {
            throw new RuntimeException(
                    "Invalid email or password"
            );
        }

        return user;
    }
}
