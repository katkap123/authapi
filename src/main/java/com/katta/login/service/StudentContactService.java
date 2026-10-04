package com.katta.login.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.katta.login.dto.CreateStudentContactRequest;
import com.katta.login.dto.StudentContactResponse;
import com.katta.login.entity.StudentContact;
import com.katta.login.repository.StudentContactRepository;
import com.katta.login.repository.UserRepository;

@Service
public class StudentContactService {

    private final StudentContactRepository studentContactRepository;
    private final UserRepository userRepository;

    public StudentContactService(
            StudentContactRepository studentContactRepository,
            UserRepository userRepository) {

        this.studentContactRepository = studentContactRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public StudentContactResponse createContact(
            UUID studentId,
            CreateStudentContactRequest request) {

        // 1. Student must exist
        if (!userRepository.existsById(studentId)) {
            throw new IllegalArgumentException(
                    "Student not found"
            );
        }

        // 2. At least email or phone must be provided
        boolean emailMissing =
                request.email() == null ||
                request.email().isBlank();

        boolean phoneMissing =
                request.phoneNumber() == null ||
                request.phoneNumber().isBlank();

        if (emailMissing && phoneMissing) {
            throw new IllegalArgumentException(
                    "At least one contact method is required"
            );
        }

        StudentContact contact = new StudentContact();

        contact.setStudentId(studentId);
        contact.setFirstName(request.firstName());
        contact.setLastName(request.lastName());
        contact.setRelationship(request.relationship());
        contact.setEmail(request.email());
        contact.setPhoneNumber(request.phoneNumber());
        contact.setPrimaryContact(request.primaryContact());
        contact.setEmailNotificationsEnabled(
                request.emailNotificationsEnabled()
        );
        contact.setSmsNotificationsEnabled(
                request.smsNotificationsEnabled()
        );

        StudentContact saved =
                studentContactRepository.save(contact);

        return toResponse(saved);
    }

    private StudentContactResponse toResponse(
            StudentContact contact) {

        return new StudentContactResponse(
                contact.getId(),
                contact.getStudentId(),
                contact.getFirstName(),
                contact.getLastName(),
                contact.getRelationship(),
                contact.getEmail(),
                contact.getPhoneNumber(),
                contact.isPrimaryContact(),
                contact.isEmailNotificationsEnabled(),
                contact.isSmsNotificationsEnabled()
        );
    }
}