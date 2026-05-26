package com.learning.rag.repository;

import com.learning.rag.entity.Document;
import com.learning.rag.entity.Document.ProcessingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, String> {
    
    List<Document> findByKnowledgeBaseIdOrderByUploadedAtDesc(String knowledgeBaseId);
    
    List<Document> findByKnowledgeBaseIdAndStatus(String knowledgeBaseId, ProcessingStatus status);
    
    void deleteByKnowledgeBaseId(String knowledgeBaseId);
}
