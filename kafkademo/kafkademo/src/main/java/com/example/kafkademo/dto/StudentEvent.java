package com.example.kafkademo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentEvent {

    private String operation; // CREATE, UPDATE, DELETE
    private Long id;          // used for UPDATE and DELETE
    private StudentDTO student;
}
