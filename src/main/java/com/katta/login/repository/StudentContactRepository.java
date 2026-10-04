package com.katta.login.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.katta.login.entity.StudentContact;

public interface StudentContactRepository
        extends JpaRepository<StudentContact, UUID> {

    List<StudentContact> findByStudentId(UUID studentId);
}