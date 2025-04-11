package com.example.enroll.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.enroll.model.Enrollment;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

}
