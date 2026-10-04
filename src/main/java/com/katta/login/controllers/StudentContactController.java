package com.katta.login.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.katta.login.dto.CreateStudentContactRequest;
import com.katta.login.dto.StudentContactResponse;
import com.katta.login.service.StudentContactService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/students")
public class StudentContactController {

    private final StudentContactService studentContactService;

    public StudentContactController(
            StudentContactService studentContactService) {
        this.studentContactService = studentContactService;
    }

    @PostMapping("/{studentId}/contacts")
    public ResponseEntity<StudentContactResponse> createContact(
            @PathVariable UUID studentId,
            @Valid @RequestBody CreateStudentContactRequest request) {

        StudentContactResponse response =
                studentContactService.createContact(
                        studentId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}