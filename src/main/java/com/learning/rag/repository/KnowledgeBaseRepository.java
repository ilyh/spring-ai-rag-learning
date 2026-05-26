package com.learning.rag.repository;

import com.learning.rag.entity.KnowledgeBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KnowledgeBaseRepository extends JpaRepository<KnowledgeBase, String> {
    
    List<KnowledgeBase> findAllByOrderByCreatedAtDesc();
    
    Optional<KnowledgeBase> findByCollectionName(String collectionName);
}
