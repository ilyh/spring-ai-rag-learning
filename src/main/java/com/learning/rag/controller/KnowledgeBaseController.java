package com.learning.rag.controller;

import com.learning.rag.dto.ChatRequest;
import com.learning.rag.dto.ChatResponse;
import com.learning.rag.dto.DocumentDTO;
import com.learning.rag.dto.KnowledgeBaseDTO;
import com.learning.rag.entity.Conversation;
import com.learning.rag.service.KnowledgeBaseService;
import com.learning.rag.service.RagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/knowledge-bases")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class KnowledgeBaseController {
    
    private final KnowledgeBaseService knowledgeBaseService;
    private final RagService ragService;
    
    // Create knowledge base
    @PostMapping
    public ResponseEntity<KnowledgeBaseDTO> createKnowledgeBase(
            @RequestParam String name,
            @RequestParam(required = false) String description) {
        KnowledgeBaseDTO kb = knowledgeBaseService.createKnowledgeBase(name, description);
        return ResponseEntity.ok(kb);
    }
    
    // Get all knowledge bases
    @GetMapping
    public ResponseEntity<List<KnowledgeBaseDTO>> getAllKnowledgeBases() {
        List<KnowledgeBaseDTO> kbs = knowledgeBaseService.getAllKnowledgeBases();
        return ResponseEntity.ok(kbs);
    }
    
    // Get knowledge base by ID
    @GetMapping("/{id}")
    public ResponseEntity<KnowledgeBaseDTO> getKnowledgeBase(@PathVariable String id) {
        KnowledgeBaseDTO kb = knowledgeBaseService.getKnowledgeBase(id);
        return ResponseEntity.ok(kb);
    }
    
    // Delete knowledge base
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKnowledgeBase(@PathVariable String id) {
        knowledgeBaseService.deleteKnowledgeBase(id);
        return ResponseEntity.noContent().build();
    }
    
    // Upload document
    @PostMapping("/{id}/documents")
    public ResponseEntity<DocumentDTO> uploadDocument(
            @PathVariable("id") String knowledgeBaseId,
            @RequestParam("file") MultipartFile file) {
        DocumentDTO document = knowledgeBaseService.uploadDocument(knowledgeBaseId, file);
        return ResponseEntity.ok(document);
    }
    
    // Get documents
    @GetMapping("/{id}/documents")
    public ResponseEntity<List<DocumentDTO>> getDocuments(@PathVariable String id) {
        List<DocumentDTO> documents = knowledgeBaseService.getDocuments(id);
        return ResponseEntity.ok(documents);
    }
    
    // Delete document
    @DeleteMapping("/{kbId}/documents/{docId}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable String kbId,
            @PathVariable String docId) {
        knowledgeBaseService.deleteDocument(kbId, docId);
        return ResponseEntity.noContent().build();
    }
    
    // Chat (non-streaming)
    @PostMapping("/{id}/chat")
    public ResponseEntity<ChatResponse> chat(
            @PathVariable("id") String knowledgeBaseId,
            @RequestBody ChatRequest request) {
        ChatResponse response = ragService.chat(knowledgeBaseId, request.getMessage(), request.getConversationId());
        return ResponseEntity.ok(response);
    }
    
    // Chat (streaming via SSE)
    @PostMapping(value = "/{id}/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(
            @PathVariable("id") String knowledgeBaseId,
            @RequestBody ChatRequest request) {
        SseEmitter emitter = new SseEmitter(300000L); // 5 minute timeout
        ragService.streamChat(knowledgeBaseId, request.getMessage(), request.getConversationId(), emitter);
        return emitter;
    }
    
    // Get conversations
    @GetMapping("/{id}/conversations")
    public ResponseEntity<List<Conversation>> getConversations(@PathVariable String id) {
        List<Conversation> conversations = ragService.getConversations(id);
        return ResponseEntity.ok(conversations);
    }
    
    // Delete conversation
    @DeleteMapping("/{kbId}/conversations/{convId}")
    public ResponseEntity<Void> deleteConversation(
            @PathVariable String kbId,
            @PathVariable String convId) {
        ragService.deleteConversation(convId);
        return ResponseEntity.noContent().build();
    }
}
