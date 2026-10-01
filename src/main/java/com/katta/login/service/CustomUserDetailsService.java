package com.katta.login.service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.katta.login.dto.UserSummaryResponse;
import com.katta.login.entity.User;
import com.katta.login.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found"
                        ));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(
                        user.getRoles()
                                .stream()
                                .map(role ->
                                        new SimpleGrantedAuthority(
                                                "ROLE_" + role.getName()
                                        )
                                )
                                .collect(Collectors.toSet())
                )
                .disabled(!user.isEnabled())
                .build();
    }

    public List<UserSummaryResponse> getUserSummaries(
        Collection<UUID> userIds) {

        return userRepository.findByIdIn(userIds)
                .stream()
                .map(user -> new UserSummaryResponse(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail()
                ))
                .toList();
        }
}
