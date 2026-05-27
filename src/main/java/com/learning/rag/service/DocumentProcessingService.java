package com.learning.rag.service;

import com.learning.rag.config.RagProperties;
import com.learning.rag.entity.Document;
import com.learning.rag.entity.KnowledgeBase;
import com.learning.rag.exception.DocumentProcessingException;
import com.learning.rag.exception.ResourceNotFoundException;
import com.learning.rag.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentProcessingService {

    private final VectorStore vectorStore;
    private final RagProperties ragProperties;
    private final DocumentRepository documentRepository;

    @Value("${app.file.upload-dir}")
    private String uploadDir;

    /**
     * Save uploaded file to disk synchronously. Returns the saved file path.
     */
    public String saveFileToDisk(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.toString();
    }

    /**
     * Process document asynchronously: extract text, split into chunks, store in vector DB.
     */
    @Async
    @Transactional
    public void processDocumentAsync(String documentId, String filePath, String fileName,
                                      String knowledgeBaseId, String collectionName) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + documentId));

        try {
            document.setStatus(Document.ProcessingStatus.PROCESSING);
            document.setFilePath(filePath);
            documentRepository.save(document);

            String text = extractText(filePath, fileName);

            List<org.springframework.ai.document.Document> chunks = splitText(text);

            chunks.forEach(chunk -> {
                chunk.getMetadata().put("document_id", documentId);
                chunk.getMetadata().put("file_name", fileName);
                chunk.getMetadata().put("knowledge_base_id", knowledgeBaseId);
                chunk.getMetadata().put("collection_name", collectionName);
            });

            vectorStore.add(chunks);

            document.setChunkCount(chunks.size());
            document.setStatus(Document.ProcessingStatus.COMPLETED);
            documentRepository.save(document);

            log.info("Document processed successfully: {}", fileName);

        } catch (Exception e) {
            log.error("Failed to process document: {}", fileName, e);
            document.setStatus(Document.ProcessingStatus.FAILED);
            documentRepository.save(document);
        }
    }

    private String extractText(String filePath, String fileName) throws IOException {
        if (fileName.toLowerCase().endsWith(".pdf")) {
            return extractFromPdf(filePath);
        } else if (fileName.toLowerCase().endsWith(".txt") ||
                   fileName.toLowerCase().endsWith(".md")) {
            return extractFromText(filePath);
        } else {
            throw new DocumentProcessingException("Unsupported file type: " + fileName);
        }
    }

    private String extractFromPdf(String filePath) throws IOException {
        try (PDDocument document = Loader.loadPDF(Path.of(filePath).toFile())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private String extractFromText(String filePath) throws IOException {
        return Files.readString(Path.of(filePath));
    }

    private List<org.springframework.ai.document.Document> splitText(String text) {
        Resource resource = new ByteArrayResource(text.getBytes());
        TextReader reader = new TextReader(resource);

        List<org.springframework.ai.document.Document> documents = reader.get();

        TokenTextSplitter splitter = new TokenTextSplitter(
            ragProperties.getChunkSize(),
            ragProperties.getChunkOverlap(),
            5,
            10000,
            true
        );

        return splitter.apply(documents);
    }

    public void deleteDocumentVectors(String documentId) {
        vectorStore.delete("document_id == '" + documentId + "'");
        log.info("Deleted vectors for document: {}", documentId);
    }

    public void deleteKnowledgeBaseVectors(String knowledgeBaseId) {
        vectorStore.delete("knowledge_base_id == '" + knowledgeBaseId + "'");
        log.info("Deleted vectors for knowledge base: {}", knowledgeBaseId);
    }
}
