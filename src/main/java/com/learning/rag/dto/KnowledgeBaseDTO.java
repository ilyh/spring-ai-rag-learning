package com.learning.rag.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeBaseDTO {
    
    private String id;
    private String name;
    private String description;
    private String collectionName;
    private String createdAt;
    private String updatedAt;
    private Long documentCount;
}
