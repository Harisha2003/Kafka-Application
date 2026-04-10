package com.example.kafkademo.controller;

import com.example.kafkademo.dto.StudentDTO;
import com.example.kafkademo.entity.Student;
import com.example.kafkademo.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    // CREATE - send to Kafka → save to H2
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody StudentDTO dto) {
        studentService.registerStudent(dto);
        return ResponseEntity.ok("Student sent to Kafka!");
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<Student>> getAll() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Student> getById(@PathVariable Long id) {
        return studentService.getStudentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable Long id, @RequestBody StudentDTO dto) {
        studentService.updateStudent(id, dto);
        return ResponseEntity.ok("Update event sent to Kafka!");
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok("Student deleted successfully!");
    }
}
