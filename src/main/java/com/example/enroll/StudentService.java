package com.example.enroll;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.enroll.model.Enrollment;
import com.example.enroll.model.Program;
import com.example.enroll.model.Student;
import com.example.enroll.repo.EnrollmentRepository;
import com.example.enroll.repo.ProgramRepository;
import com.example.enroll.repo.StudentRepository;


@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private ProgramRepository programRepository;
    
    @Autowired
    private EnrollmentRepository enrollmentRepository; // Add this line    
	
    // New student registration
    public Student registerStudent(Student student) {
        return studentRepository.save(student);
    }    
    
    // Validation of user credentials
    public boolean validateUser(String username, String password) {
        Optional<Student> studentOptional = studentRepository.findByUsername(username);
        if (studentOptional.isPresent()) {
            Student student = studentOptional.get();
            
            return password.equals(student.getPassword());
        }
        return false; // if user not found
    }
    
    public Optional<Program> findProgramById(Long programCode) {
        return programRepository.findById(programCode);
    }
    public List<Program> findAllProgram() {
    	return programRepository.findAll();
    }

    public Enrollment saveEnrollment(Enrollment enrollment) {
        return enrollmentRepository.save(enrollment);
    }    
    
    public Optional<Student> findByUsername(String username) {
        return studentRepository.findByUsername(username);
    }    

    public Optional<Student> findStudentById(Long studentId) {
        return studentRepository.findById(studentId);
    }
    
    public Student updateStudent(Student student) {
        return studentRepository.save(student); 
    }
    
}