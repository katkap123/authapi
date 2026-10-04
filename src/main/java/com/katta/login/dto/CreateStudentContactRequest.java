package com.katta.login.dto;

import com.katta.login.entity.ContactRelationship;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateStudentContactRequest(

        @NotBlank
        String firstName,

        String lastName,

        @NotNull
        ContactRelationship relationship,

        String email,

        String phoneNumber,

        boolean primaryContact,

        boolean emailNotificationsEnabled,

        boolean smsNotificationsEnabled
) {
}