package com.katta.login.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.katta.login.dto.CreateStudentContactRequest;
import com.katta.login.dto.StudentContactResponse;
import com.katta.login.entity.StudentContact;
import com.katta.login.entity.User;
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
        User student = userRepository
                .findById(studentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Student not found")
                );

        boolean isStudent;
        isStudent = student.getRoles()
                .stream()
                .anyMatch(role ->
                        "STUDENT".equalsIgnoreCase(role.getName())
                );

        if (!isStudent) {
        throw new IllegalArgumentException(
                "User is not a student"
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

        if (request.primaryContact()) {

        List<StudentContact> existingPrimaryContacts =
                studentContactRepository
                        .findByStudentIdAndPrimaryContactTrue(studentId);

        for (StudentContact existingContact : existingPrimaryContacts) {
                existingContact.setPrimaryContact(false);
        }

        studentContactRepository.saveAll(existingPrimaryContacts);
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

        @Transactional(readOnly = true)
        public List<StudentContactResponse> getContacts(UUID studentId) {

        if (!userRepository.existsById(studentId)) {
                throw new IllegalArgumentException("Student not found");
        }

        return studentContactRepository
                .findByStudentId(studentId)
                .stream()
                .map(this::toResponse)
                .toList();
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