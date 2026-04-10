package com.example.kafkademo.controller;

import com.example.kafkademo.dto.StudentDTO;
import com.example.kafkademo.entity.Student;
import com.example.kafkademo.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;
     //sample request data
    private StudentDTO dto() { return new StudentDTO("Alice", "Math"); }

    private Student student(Long id) {
        Student s = new Student();
        s.setId(id);
        s.setName("Alice");
        s.setCourse("Math");
        return s;
    }

    @Test
    void register_returnsOk() throws Exception {
        mockMvc.perform(post("/students/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto())))
                .andExpect(status().isOk())
                .andExpect(content().string("Student sent to Kafka!"));

        verify(studentService).registerStudent(dto());
    }

    @Test
    void getAll_returnsStudentList() throws Exception {
        when(studentService.getAllStudents()).thenReturn(List.of(student(1L)));

        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[0].course").value("Math"));
    }


    @Test
    void getById_notFound_returns404() throws Exception {
        when(studentService.getStudentById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/students/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_returnsOk() throws Exception {
        mockMvc.perform(put("/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto())))
                .andExpect(status().isOk())
                .andExpect(content().string("Update event sent to Kafka!"));

        verify(studentService).updateStudent(1L, dto());
    }

    @Test
    void delete_returnsOk() throws Exception {
        mockMvc.perform(delete("/students/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Student deleted successfully!"));

        verify(studentService).deleteStudent(1L);
    }
}
