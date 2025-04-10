package com.example.enroll.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.enroll.model.Enrollment;
import com.example.enroll.model.LoginForm;
import com.example.enroll.model.Program;
import com.example.enroll.StudentRegistration;
import com.example.enroll.model.Student;
import jakarta.servlet.http.HttpSession;

@Controller
public class StudentController {

    @Autowired
    private StudentRegistration studentRegistration;
    
 // Inject HttpSession
    @Autowired
    private HttpSession session;    
	
    // Show registration form
    @GetMapping("/")
    public String showRegistrationForm(Model model) {
        model.addAttribute("student", new Student()); 
        model.addAttribute("loginForm", new LoginForm()); 
        return "index"; 
    }
    
    @PostMapping("/students/login")
    public String login(@ModelAttribute LoginForm login, Model model, HttpSession session) {
        // Verify user credentials
        if (studentRegistration.validateUser(login.getUsername(), login.getPassword())) {
            // Successful login
            Optional<Student> studentOpt = studentRegistration.findByUsername(login.getUsername());
            if (studentOpt.isPresent()) {
                session.setAttribute("studentId", studentOpt.get().getStudentId()); 
            }
            return "redirect:/students/programs"; 
        }

        // login fails
        model.addAttribute("student", new Student());          
        model.addAttribute("loginForm", login);            
        model.addAttribute("loginError", "Invalid input !"); // error message
        return "index"; 
    }   
    
    // Handle registration form submission
    @PostMapping("/students/register")
    public String registerStudent(@ModelAttribute Student student) {
        studentRegistration.registerStudent(student);
        return "success"; // Redirect after successful registration
    }
    
    // List all programs
    @GetMapping("/students/programs")
    public String listPrograms(Model model) {
        model.addAttribute("programs", studentRegistration.findAllProgram());
        return "programs"; 
    }    

    @PostMapping("/students/confirmation")
    public String confirmPrograms(@RequestParam("selectedPrograms") List<String> selectedProgramCodes, Model model) {
        List<Program> selectedPrograms = new ArrayList<>();
        for (String code : selectedProgramCodes) {
            Optional<Program> programOptional = studentRegistration.findProgramById(Long.valueOf(code));
            programOptional.ifPresent(selectedPrograms::add);
        }

        double totalFee = selectedPrograms.stream()
            .mapToDouble(Program::getFee)
            .sum();

        model.addAttribute("selectedPrograms", selectedPrograms);
        model.addAttribute("totalFee", totalFee);
        
       
        model.addAttribute("totalFee", totalFee);
        
        return "confirmation"; 
    }
    
    @PostMapping("/students/save")
    public String saveEnrollment(@RequestParam("selectedPrograms") List<String> selectedProgramCodes,
                                 @RequestParam("programFees") List<Double> programFees, // Receive program fees
                                 @RequestParam("totalfee") double totalFee, 
                                 Model model) {
        
        // Retrieve the student ID from the session
        Long studentId = (Long) session.getAttribute("studentId");

        if (studentId != null) {
            for (int i = 0; i < selectedProgramCodes.size(); i++) {
                String code = selectedProgramCodes.get(i);
                Double fee = programFees.get(i); // Get the fee associated with the program code

                try {
                    Long programCode = Long.valueOf(code);
                    
                    Enrollment enrollment = new Enrollment();
                    enrollment.setStudentId(studentId);
                    enrollment.setProgramCode(programCode);
                    enrollment.setAmountPaid(fee); // Set fee from the programFees list
                    enrollment.setStartDate(new Date());
                    enrollment.setStatus("Enrolled");

                    studentRegistration.saveEnrollment(enrollment);
                } catch (NumberFormatException e) {
                    model.addAttribute("error", "Invalid program code: " + code);
                    return "error"; 
                }
            }
            
            return "enrollment"; 
        } else {
            model.addAttribute("error", "User not found");
            return "error"; 
        }
    }
    
    @GetMapping("/students/profile")
    public String editProfile(Model model, HttpSession session) {
        Long studentId = (Long) session.getAttribute("studentId");
        if (studentId != null) {
            Optional<Student> studentOpt = studentRegistration.findStudentById(studentId); // retrieve student by student ID
            if (studentOpt.isPresent()) {
                model.addAttribute("student", studentOpt.get());
                return "profile"; // Return to profile
            }
        }
        model.addAttribute("error", "User not found");
        return "error"; 
    }

    @PostMapping("/students/update")
    public String updateProfile(@ModelAttribute Student student, HttpSession session) {
        Long studentId = (Long) session.getAttribute("studentId");
        if (studentId != null) {
            Optional<Student> existingStudentOpt = studentRegistration.findStudentById(studentId);
            if (existingStudentOpt.isPresent()) {
                Student existingStudent = existingStudentOpt.get();

                // Retain existing password if the new one is not provided
                if (student.getPassword() == null || student.getPassword().isEmpty()) {
                    student.setPassword(existingStudent.getPassword());
                }

                // Update the student details
                student.setStudentId(studentId);
                studentRegistration.updateStudent(student);
                return "enrollment"; // Redirect to success page after update
            }
        }
        return "error"; // Handle error case
    }    
    
}