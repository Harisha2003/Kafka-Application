package com.example.kafkademo.service;

import com.example.kafkademo.dto.StudentDTO;
import com.example.kafkademo.entity.Student;
import com.example.kafkademo.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentProducer studentProducer;

    @InjectMocks
    private StudentService studentService;

    private final StudentDTO dto = new StudentDTO("Alice", "Math");

    private Student savedStudent(Long id) {
        Student s = new Student();
        s.setId(id);
        s.setName("Alice");
        s.setCourse("Math");
        return s;
    }

    @Test
    void registerStudent_sendsCreateEvent() {
        studentService.registerStudent(dto);
        verify(studentProducer).sendEvent("CREATE", null, dto);
    }

    @Test
    void saveStudent_persistsStudent() {
        studentService.saveStudent(dto);

        ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Alice");
        assertThat(captor.getValue().getCourse()).isEqualTo("Math");
    }

    @Test
    void getStudentById_found_returnsOptional() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(savedStudent(1L)));

        Optional<Student> result = studentService.getStudentById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    void getStudentById_notFound_returnsEmpty() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(studentService.getStudentById(99L)).isEmpty();
    }

    @Test
    void updateStudent_sendsUpdateEvent() {
        studentService.updateStudent(1L, dto);
        verify(studentProducer).sendEvent("UPDATE", 1L, dto);
    }

    @Test
    void applyUpdate_updatesAndSavesStudent() {
        Student existing = savedStudent(1L);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(studentRepository.save(existing)).thenReturn(existing);

        StudentDTO updateDto = new StudentDTO("Bob", "Science");
        Student result = studentService.applyUpdate(1L, updateDto);

        assertThat(result.getName()).isEqualTo("Bob");
        assertThat(result.getCourse()).isEqualTo("Science");
        verify(studentRepository).save(existing);
    }


    @Test
    void deleteStudent_sendsDeleteEvent() {
        studentService.deleteStudent(1L);
        verify(studentProducer).sendEvent("DELETE", 1L, null);
    }

    @Test
    void applyDelete_deletesById() {
        studentService.applyDelete(1L);
        verify(studentRepository).deleteById(1L);
    }
}
