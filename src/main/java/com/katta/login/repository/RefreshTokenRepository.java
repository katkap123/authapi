package com.katta.login.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.katta.login.entity.RefreshToken;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);
    List<RefreshToken> findByFamilyId(UUID familyId);
    boolean existsByFamilyIdAndRevokedFalse(UUID familyId);
    @Modifying
    @Query("""
        UPDATE RefreshToken r
        SET r.revoked = true
        WHERE r.familyId = :familyId
        AND r.revoked = false
    """)
    int revokeAllByFamilyId(@Param("familyId") UUID familyId);
}