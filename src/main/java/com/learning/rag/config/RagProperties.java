package com.learning.rag.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.rag")
public class RagProperties {
    
    private int chunkSize = 800;
    private int chunkOverlap = 100;
    private int topK = 5;
    private double similarityThreshold = 0.7;
    /**
     * Max conversation history turns to include in prompt (1 turn = 1 user + 1 assistant message).
     */
    private int maxHistoryTurns = 3;
}
