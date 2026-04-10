package com.example.kafkademo.service;

import com.example.kafkademo.dto.StudentDTO;
import com.example.kafkademo.dto.StudentEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentConsumerTest {

    @Mock
    private StudentService studentService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private StudentConsumer studentConsumer;

    private final StudentDTO studentDTO = new StudentDTO("Alice", "Math");

    @Test
    void consume_createOperation_callsSaveStudent() throws Exception {
        StudentEvent event = new StudentEvent("CREATE", null, studentDTO);
        when(objectMapper.readValue(anyString(), eq(StudentEvent.class))).thenReturn(event);

        studentConsumer.consume("{\"operation\":\"CREATE\",\"student\":{\"name\":\"Alice\",\"course\":\"Math\"}}");

        verify(studentService).saveStudent(studentDTO);
        verifyNoMoreInteractions(studentService);
    }

    @Test
    void consume_updateOperation_callsApplyUpdate() throws Exception {
        StudentEvent event = new StudentEvent("UPDATE", 1L, studentDTO);
        when(objectMapper.readValue(anyString(), eq(StudentEvent.class))).thenReturn(event);

        studentConsumer.consume("{\"operation\":\"UPDATE\",\"id\":1,\"student\":{\"name\":\"Alice\",\"course\":\"Math\"}}");

        verify(studentService).applyUpdate(1L, studentDTO);
        verifyNoMoreInteractions(studentService);
    }

    @Test
    void consume_deleteOperation_callsApplyDelete() throws Exception {
        StudentEvent event = new StudentEvent("DELETE", 2L, null);
        when(objectMapper.readValue(anyString(), eq(StudentEvent.class))).thenReturn(event);

        studentConsumer.consume("{\"operation\":\"DELETE\",\"id\":2}");

        verify(studentService).applyDelete(2L);
        verifyNoMoreInteractions(studentService);
    }

    @Test
    void consume_unknownOperation_noServiceInteraction() throws Exception {
        StudentEvent event = new StudentEvent("UNKNOWN", null, null);
        when(objectMapper.readValue(anyString(), eq(StudentEvent.class))).thenReturn(event);

        studentConsumer.consume("{\"operation\":\"UNKNOWN\"}");

        verifyNoInteractions(studentService);
    }

    @Test
    void consume_invalidJson_doesNotThrow() throws Exception {
        when(objectMapper.readValue(anyString(), eq(StudentEvent.class)))
                .thenThrow(new RuntimeException("parse error"));

        studentConsumer.consume("invalid-json");

        verifyNoInteractions(studentService);
    }
}
