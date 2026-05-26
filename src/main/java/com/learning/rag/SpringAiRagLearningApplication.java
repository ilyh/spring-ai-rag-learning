package com.learning.rag;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SpringAiRagLearningApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SpringAiRagLearningApplication.class, args);
    }
}
