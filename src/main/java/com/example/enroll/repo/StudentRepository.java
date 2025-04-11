package com.example.enroll.repo;


import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.enroll.model.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
	Optional<Student> findByUsername(String username);
}