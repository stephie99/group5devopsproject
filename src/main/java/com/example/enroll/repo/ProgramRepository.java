package com.example.enroll.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.enroll.model.Program;

public interface ProgramRepository extends JpaRepository<Program, Long> {
	Optional<Program> findById(Long programCode); 
}
