package com.learning.rag.service;

import com.learning.rag.dto.DocumentDTO;
import com.learning.rag.dto.KnowledgeBaseDTO;
import com.learning.rag.entity.Document;
import com.learning.rag.entity.KnowledgeBase;
import com.learning.rag.exception.ResourceNotFoundException;
import com.learning.rag.repository.ConversationRepository;
import com.learning.rag.repository.DocumentRepository;
import com.learning.rag.repository.KnowledgeBaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeBaseService {
    
    private final KnowledgeBaseRepository knowledgeBaseRepository;
    private final DocumentRepository documentRepository;
    private final ConversationRepository conversationRepository;
    private final DocumentProcessingService documentProcessingService;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public KnowledgeBaseDTO createKnowledgeBase(String name, String description) {
        KnowledgeBase kb = new KnowledgeBase();
        kb.setName(name);
        kb.setDescription(description);
        kb.setCollectionName("kb_" + System.currentTimeMillis());
        
        kb = knowledgeBaseRepository.save(kb);
        
        return convertToDTO(kb, 0L);
    }
    
    public List<KnowledgeBaseDTO> getAllKnowledgeBases() {
        return knowledgeBaseRepository.findAllByOrderByCreatedAtDesc().stream()
            .map(kb -> {
                long docCount = documentRepository.findByKnowledgeBaseIdOrderByUploadedAtDesc(kb.getId()).size();
                return convertToDTO(kb, docCount);
            })
            .collect(Collectors.toList());
    }
    
    public KnowledgeBaseDTO getKnowledgeBase(String id) {
        KnowledgeBase kb = knowledgeBaseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Knowledge base not found: " + id));
        
        long docCount = documentRepository.findByKnowledgeBaseIdOrderByUploadedAtDesc(id).size();
        return convertToDTO(kb, docCount);
    }
    
    @Transactional
    public void deleteKnowledgeBase(String id) {
        KnowledgeBase kb = knowledgeBaseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Knowledge base not found: " + id));

        // Delete all vectors from Milvus before removing metadata.
        documentProcessingService.deleteKnowledgeBaseVectors(id);
        
        // Delete all conversations
        conversationRepository.deleteByKnowledgeBaseId(id);
        
        // Delete all documents
        documentRepository.deleteByKnowledgeBaseId(id);
        
        // Delete knowledge base
        knowledgeBaseRepository.delete(kb);
        
        log.info("Deleted knowledge base: {}", id);
    }
    
    @Transactional
    public DocumentDTO uploadDocument(String knowledgeBaseId, MultipartFile file) {
        KnowledgeBase kb = knowledgeBaseRepository.findById(knowledgeBaseId)
            .orElseThrow(() -> new ResourceNotFoundException("Knowledge base not found: " + knowledgeBaseId));

        // Create document entity
        Document document = new Document();
        document.setFileName(file.getOriginalFilename());
        document.setFileType(getFileExtension(file.getOriginalFilename()));
        document.setFileSize(file.getSize());
        document.setKnowledgeBase(kb);
        document.setStatus(Document.ProcessingStatus.PENDING);

        document = documentRepository.saveAndFlush(document);

        try {
            // Save file to disk synchronously
            String filePath = documentProcessingService.saveFileToDisk(file);

            // Update status and file path
            document.setFilePath(filePath);
            document.setStatus(Document.ProcessingStatus.PROCESSING);
            document = documentRepository.saveAndFlush(document);

            // Process document asynchronously
            documentProcessingService.processDocumentAsync(
                document.getId(), filePath, document.getFileName(),
                kb.getId(), kb.getCollectionName());

        } catch (IOException e) {
            log.error("Failed to save file: {}", document.getFileName(), e);
            document.setStatus(Document.ProcessingStatus.FAILED);
            document = documentRepository.saveAndFlush(document);
        }

        return convertToDTO(document);
    }
    
    public List<DocumentDTO> getDocuments(String knowledgeBaseId) {
        return documentRepository.findByKnowledgeBaseIdOrderByUploadedAtDesc(knowledgeBaseId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public void deleteDocument(String knowledgeBaseId, String documentId) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + documentId));
        
        if (!document.getKnowledgeBase().getId().equals(knowledgeBaseId)) {
            throw new RuntimeException("Document does not belong to this knowledge base");
        }
        
        // Delete vectors from Milvus
        documentProcessingService.deleteDocumentVectors(documentId);
        
        // Delete document
        documentRepository.delete(document);
        
        log.info("Deleted document: {}", documentId);
    }
    
    private KnowledgeBaseDTO convertToDTO(KnowledgeBase kb, Long docCount) {
        KnowledgeBaseDTO dto = new KnowledgeBaseDTO();
        dto.setId(kb.getId());
        dto.setName(kb.getName());
        dto.setDescription(kb.getDescription());
        dto.setCollectionName(kb.getCollectionName());
        dto.setCreatedAt(kb.getCreatedAt().format(FORMATTER));
        dto.setUpdatedAt(kb.getUpdatedAt().format(FORMATTER));
        dto.setDocumentCount(docCount);
        return dto;
    }
    
    private DocumentDTO convertToDTO(Document doc) {
        DocumentDTO dto = new DocumentDTO();
        dto.setId(doc.getId());
        dto.setFileName(doc.getFileName());
        dto.setFileType(doc.getFileType());
        dto.setFileSize(doc.getFileSize());
        dto.setChunkCount(doc.getChunkCount());
        dto.setUploadedAt(doc.getUploadedAt() != null ? doc.getUploadedAt().format(FORMATTER) : null);
        dto.setStatus(doc.getStatus() != null ? doc.getStatus().name() : null);
        return dto;
    }
    
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }
}
