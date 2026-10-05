package com.katta.login.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.katta.login.dto.StudentContactResponse;
import com.katta.login.service.StudentContactService;

@RestController
@RequestMapping("/api/internal/students")
public class InternalStudentContactController {

    private final StudentContactService studentContactService;

    public InternalStudentContactController(
            StudentContactService studentContactService) {
        this.studentContactService = studentContactService;
    }

    @GetMapping("/{studentId}/contacts")
    public ResponseEntity<List<StudentContactResponse>> getContacts(
            @PathVariable UUID studentId) {

        return ResponseEntity.ok(
                studentContactService.getContacts(studentId)
        );
    }
}