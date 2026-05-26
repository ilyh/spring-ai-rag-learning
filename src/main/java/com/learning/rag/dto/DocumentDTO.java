package com.learning.rag.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDTO {
    
    private String id;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private Integer chunkCount;
    private String uploadedAt;
    private String status;
}
