package com.katta.login.dto;

import java.util.UUID;

import com.katta.login.entity.ContactRelationship;

public record StudentContactResponse(
        UUID id,
        UUID studentId,
        String firstName,
        String lastName,
        ContactRelationship relationship,
        String email,
        String phoneNumber,
        boolean primaryContact,
        boolean emailNotificationsEnabled,
        boolean smsNotificationsEnabled
) {
}