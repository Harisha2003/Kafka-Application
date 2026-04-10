package com.example.kafkademo.service;

import com.example.kafkademo.dto.StudentDTO;
import com.example.kafkademo.entity.Student;
import com.example.kafkademo.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentProducer studentProducer;

    @Autowired
    private StudentRepository studentRepository;

    // CREATE - sends CREATE event to Kafka
    public void registerStudent(StudentDTO dto) {
        studentProducer.sendEvent("CREATE", null, dto);
    }

    // Called by Consumer for CREATE
    public void saveStudent(StudentDTO dto) {
        Student student = new Student();
        student.setName(dto.getName());
        student.setCourse(dto.getCourse());
        studentRepository.save(student);
    }

    // READ ALL
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // READ BY ID
    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    // UPDATE - sends UPDATE event to Kafka (called by Controller)
    public void updateStudent(Long id, StudentDTO dto) {
        studentProducer.sendEvent("UPDATE", id, dto);
    }

    // Called by Consumer for UPDATE - only saves to DB, no Kafka event
    public Student applyUpdate(Long id, StudentDTO dto) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
        student.setName(dto.getName());
        student.setCourse(dto.getCourse());
        return studentRepository.save(student);
    }

    // DELETE - sends DELETE event to Kafka (called by Controller)
    public void deleteStudent(Long id) {
        studentProducer.sendEvent("DELETE", id, null);
    }

    // Called by Consumer for DELETE - only deletes from DB, no Kafka event
    public void applyDelete(Long id) {
        studentRepository.deleteById(id);
    }
}
