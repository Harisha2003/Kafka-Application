package com.example.kafkademo.service;

import com.example.kafkademo.dto.StudentEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class StudentConsumer {

    private static final Logger logger = LoggerFactory.getLogger(StudentConsumer.class);

    @Autowired
    private StudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "student-topic", groupId = "student-group")
    public void consume(String message) {
        try {
            StudentEvent event = objectMapper.readValue(message, StudentEvent.class);
            logger.info("Consumed [{}] event - id: {}", event.getOperation(), event.getId());

            switch (event.getOperation()) {
                case "CREATE" -> studentService.saveStudent(event.getStudent());
                case "UPDATE" -> studentService.applyUpdate(event.getId(), event.getStudent());
                case "DELETE" -> studentService.applyDelete(event.getId());
                default -> logger.warn("Unknown operation: {}", event.getOperation());
            }
        } catch (Exception e) {
            logger.error("Failed to process event: {}", e.getMessage());
        }
    }
}
