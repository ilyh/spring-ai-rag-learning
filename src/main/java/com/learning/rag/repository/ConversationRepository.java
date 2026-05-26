package com.learning.rag.repository;

import com.learning.rag.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, String> {
    
    List<Conversation> findByKnowledgeBaseIdOrderByUpdatedAtDesc(String knowledgeBaseId);
    
    void deleteByKnowledgeBaseId(String knowledgeBaseId);
}
