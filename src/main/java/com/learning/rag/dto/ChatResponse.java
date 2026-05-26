package com.learning.rag.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    
    private String conversationId;
    private String answer;
    private List<Source> sources;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Source {
        private String documentId;
        private String fileName;
        private String content;
        private Double score;
    }
}
