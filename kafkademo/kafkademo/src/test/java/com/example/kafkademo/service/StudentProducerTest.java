package com.example.kafkademo.service;

import com.example.kafkademo.dto.StudentDTO;
import com.example.kafkademo.dto.StudentEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private StudentProducer studentProducer;

    private final StudentDTO dto = new StudentDTO("Alice", "Math");

    @Test
    void sendEvent_create_sendsSerializedMessageToKafka() throws Exception {
        StudentEvent event = new StudentEvent("CREATE", null, dto);
        when(objectMapper.writeValueAsString(event)).thenReturn("{\"operation\":\"CREATE\"}");

        studentProducer.sendEvent("CREATE", null, dto);

        verify(kafkaTemplate).send("student-topic", "{\"operation\":\"CREATE\"}");
    }

    @Test
    void sendEvent_update_sendsSerializedMessageToKafka() throws Exception {
        StudentEvent event = new StudentEvent("UPDATE", 1L, dto);
        when(objectMapper.writeValueAsString(event)).thenReturn("{\"operation\":\"UPDATE\"}");

        studentProducer.sendEvent("UPDATE", 1L, dto);

        verify(kafkaTemplate).send("student-topic", "{\"operation\":\"UPDATE\"}");
    }

    @Test
    void sendEvent_delete_sendsSerializedMessageToKafka() throws Exception {
        StudentEvent event = new StudentEvent("DELETE", 1L, null);
        when(objectMapper.writeValueAsString(event)).thenReturn("{\"operation\":\"DELETE\"}");

        studentProducer.sendEvent("DELETE", 1L, null);

        verify(kafkaTemplate).send("student-topic", "{\"operation\":\"DELETE\"}");
    }

}
