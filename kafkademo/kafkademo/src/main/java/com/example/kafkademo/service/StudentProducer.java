package com.example.kafkademo.service;

import com.example.kafkademo.dto.StudentDTO;
import com.example.kafkademo.dto.StudentEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class StudentProducer {

    private static final Logger logger = LoggerFactory.getLogger(StudentProducer.class);
    private static final String TOPIC = "student-topic";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendEvent(String operation, Long id, StudentDTO dto) {
        try {
            StudentEvent event = new StudentEvent(operation, id, dto);
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, message);
            logger.info("Produced [{}] event - id: {}, name: {}, course: {}",
                    operation, id,
                    dto != null ? sanitize(dto.getName()) : "null",
                    dto != null ? sanitize(dto.getCourse()) : "null");
        } catch (Exception e) {
            logger.error("Failed to produce event: {}", e.getMessage());
        }
    }

    private String sanitize(String input) {
        if (input == null) return "null";
        return input.replaceAll("[\\r\\n\\t]", "_");
    }
}
