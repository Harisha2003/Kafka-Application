package com.example.kafkademo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.test.EmbeddedKafkaZKBroker;

@Configuration
public class EmbeddedKafkaConfig {

    @Bean
    public EmbeddedKafkaZKBroker embeddedKafkaBroker() {
        EmbeddedKafkaZKBroker broker = new EmbeddedKafkaZKBroker(1, true, 1, "student-topic");
        broker.kafkaPorts(9092);
        return broker;
    }
}
