package com.learning.rag.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.rag.config.RagProperties;
import com.learning.rag.dto.ChatResponse;
import com.learning.rag.entity.Conversation;
import com.learning.rag.entity.KnowledgeBase;
import com.learning.rag.entity.Message;
import com.learning.rag.exception.AIServiceException;
import com.learning.rag.exception.ResourceNotFoundException;
import com.learning.rag.repository.ConversationRepository;
import com.learning.rag.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RagService {
    
    private final VectorStore vectorStore;
    private final ChatClient chatClient;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final RagProperties ragProperties;
    private final ObjectMapper objectMapper;
    
    @Transactional
    public ChatResponse chat(String knowledgeBaseId, String question, String conversationId) {
        try {
            // Retrieve relevant documents
            List<Document> relevantDocs = retrieveRelevantDocuments(question, knowledgeBaseId);
            
            // Build context from retrieved documents
            String context = buildContext(relevantDocs);
            
            // Load conversation history if conversationId is provided
            List<Message> historyMessages = loadConversationHistory(conversationId);
            
            // Generate answer using LLM with conversation history
            String answer = generateAnswer(question, context, historyMessages);
            
            // Save conversation and messages
            Conversation conversation = saveConversationAndMessages(
                knowledgeBaseId, question, answer, relevantDocs, conversationId
            );
            
            // Build response
            return buildChatResponse(conversation.getId(), answer, relevantDocs);
            
        } catch (Exception e) {
            log.error("Error during chat processing", e);
            throw new AIServiceException("Failed to process chat request: " + e.getMessage(), e);
        }
    }
    
    private List<Document> retrieveRelevantDocuments(String question, String knowledgeBaseId) {
        SearchRequest searchRequest = SearchRequest.builder()
            .query(question)
            .topK(ragProperties.getTopK())
            .similarityThreshold(ragProperties.getSimilarityThreshold())
            .filterExpression(String.format("knowledge_base_id == '%s'", knowledgeBaseId))
            .build();
        
        return vectorStore.similaritySearch(searchRequest);
    }
    
    private String buildContext(List<Document> documents) {
        if (documents.isEmpty()) {
            return "No relevant information found in the knowledge base.";
        }
        
        return documents.stream()
            .map(doc -> String.format("[Document: %s]\n%s\n", 
                doc.getMetadata().getOrDefault("file_name", "Unknown"),
                doc.getText()))
            .collect(Collectors.joining("\n---\n"));
    }
    
    /**
     * Load conversation history messages for multi-turn dialogue.
     * Only keeps the most recent N turns (1 turn = 1 user + 1 assistant) to avoid overly long prompts.
     */
    private List<Message> loadConversationHistory(String conversationId) {
        if (conversationId == null || conversationId.isEmpty()) {
            return Collections.emptyList();
        }
        List<Message> allMessages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
        // Limit to the most recent N turns: each turn = 2 messages (user + assistant)
        int maxMessages = ragProperties.getMaxHistoryTurns() * 2;
        if (allMessages.size() > maxMessages) {
            return allMessages.subList(allMessages.size() - maxMessages, allMessages.size());
        }
        return allMessages;
    }
    
    private String generateAnswer(String question, String context, List<Message> historyMessages) {
        String systemPrompt = """
            You are a knowledgeable assistant. Please answer the question based on the provided context and conversation history.
            If the context doesn't contain relevant information, please clearly state that.
            """;
        
        String contextBlock = "Context:\n" + context;
        
        // Build message list: system context + history + current question
        List<org.springframework.ai.chat.messages.Message> chatMessages = new ArrayList<>();
        
        // Add context as system message
        chatMessages.add(new org.springframework.ai.chat.messages.SystemMessage(systemPrompt + "\n\n" + contextBlock));
        
        // Add conversation history
        for (Message historyMsg : historyMessages) {
            if (historyMsg.getType() == Message.MessageType.USER) {
                chatMessages.add(new UserMessage(historyMsg.getContent()));
            } else if (historyMsg.getType() == Message.MessageType.ASSISTANT) {
                chatMessages.add(new AssistantMessage(historyMsg.getContent()));
            }
        }
        
        // Add current question
        chatMessages.add(new UserMessage(question));
        
        Prompt prompt = new Prompt(chatMessages);
        log.info("RAG prompt: history messages={}, total messages={}", historyMessages.size(), chatMessages.size());
        
        try {
            AssistantMessage response = chatClient.prompt(prompt)
                .call()
                .chatResponse()
                .getResult()
                .getOutput();
            
            return response.getText();
        } catch (Exception e) {
            throw new AIServiceException("Failed to generate answer from AI service", e);
        }
    }
    
    @Transactional
    public Conversation saveConversationAndMessages(
            String knowledgeBaseId, 
            String question, 
            String answer,
            List<Document> sources,
            String conversationId) {
        
        Conversation conversation;
        
        if (conversationId != null && !conversationId.isEmpty()) {
            conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found: " + conversationId));
        } else {
            KnowledgeBase kb = new KnowledgeBase();
            kb.setId(knowledgeBaseId);
            
            conversation = new Conversation();
            conversation.setKnowledgeBase(kb);
            conversation.setTitle(question.length() > 50 ? question.substring(0, 50) + "..." : question);
            conversation = conversationRepository.save(conversation);
        }
        
        // Save user message
        Message userMessage = new Message();
        userMessage.setConversation(conversation);
        userMessage.setType(Message.MessageType.USER);
        userMessage.setContent(question);
        messageRepository.save(userMessage);
        
        // Save assistant message with sources
        Message assistantMessage = new Message();
        assistantMessage.setConversation(conversation);
        assistantMessage.setType(Message.MessageType.ASSISTANT);
        assistantMessage.setContent(answer);
        assistantMessage.setSources(serializeSources(sources));
        messageRepository.save(assistantMessage);
        
        return conversation;
    }
    
    private String serializeSources(List<Document> documents) {
        List<Map<String, Object>> sourceList = documents.stream()
            .map(doc -> {
                Map<String, Object> source = new HashMap<>();
                source.put("documentId", doc.getMetadata().get("document_id"));
                source.put("fileName", doc.getMetadata().get("file_name"));
                source.put("content", doc.getText());
                source.put("score", doc.getScore());
                return source;
            })
            .collect(Collectors.toList());

        try {
            return objectMapper.writeValueAsString(sourceList);
        } catch (Exception e) {
            log.error("Failed to serialize sources to JSON", e);
            return "[]";
        }
    }
    
    private ChatResponse buildChatResponse(String conversationId, String answer, List<Document> sources) {
        List<ChatResponse.Source> sourceList = sources.stream()
            .map(doc -> new ChatResponse.Source(
                (String) doc.getMetadata().get("document_id"),
                (String) doc.getMetadata().get("file_name"),
                doc.getText(),
                doc.getScore()
            ))
            .collect(Collectors.toList());
        
        return new ChatResponse(conversationId, answer, sourceList);
    }
    
    /**
     * Streaming chat: sends text chunks to the client via SseEmitter as they are generated.
     * After the stream completes, saves the conversation and messages.
     */
    public void streamChat(String knowledgeBaseId, String question, String conversationId, SseEmitter emitter) {
        // Retrieve relevant documents (synchronous, fast)
        List<Document> relevantDocs = retrieveRelevantDocuments(question, knowledgeBaseId);
        String context = buildContext(relevantDocs);
        List<Message> historyMessages = loadConversationHistory(conversationId);

        // Build prompt
        String systemPrompt = """
            You are a knowledgeable assistant. Please answer the question based on the provided context and conversation history.
            If the context doesn't contain relevant information, please clearly state that.
            """;

        String contextBlock = "Context:\n" + context;

        List<org.springframework.ai.chat.messages.Message> chatMessages = new ArrayList<>();
        chatMessages.add(new org.springframework.ai.chat.messages.SystemMessage(systemPrompt + "\n\n" + contextBlock));

        for (Message historyMsg : historyMessages) {
            if (historyMsg.getType() == Message.MessageType.USER) {
                chatMessages.add(new UserMessage(historyMsg.getContent()));
            } else if (historyMsg.getType() == Message.MessageType.ASSISTANT) {
                chatMessages.add(new AssistantMessage(historyMsg.getContent()));
            }
        }
        chatMessages.add(new UserMessage(question));

        Prompt prompt = new Prompt(chatMessages);
        log.info("RAG stream prompt: history messages={}, total messages={}", historyMessages.size(), chatMessages.size());

        // Use StringBuilder to collect the full answer as chunks arrive
        StringBuilder fullAnswer = new StringBuilder();

        // Subscribe to the Flux stream and push each chunk to SseEmitter
        chatClient.prompt(prompt)
            .stream()
            .chatResponse()
            .subscribe(
                chatResponse -> {
                    if (chatResponse.getResult() != null && chatResponse.getResult().getOutput() != null) {
                        String text = chatResponse.getResult().getOutput().getText();
                        if (text != null && !text.isEmpty()) {
                            fullAnswer.append(text);
                            try {
                                emitter.send(SseEmitter.event().data(text));
                            } catch (Exception e) {
                                log.error("Error sending SSE event", e);
                                emitter.completeWithError(e);
                            }
                        }
                    }
                },
                error -> {
                    log.error("Error during stream chat processing", error);
                    emitter.completeWithError(error);
                },
                () -> {
                    // Save conversation and messages after stream completes
                    try {
                        saveConversationAndMessages(
                            knowledgeBaseId, question, fullAnswer.toString(), relevantDocs, conversationId
                        );
                        log.info("Stream chat completed, answer length: {}", fullAnswer.length());
                    } catch (Exception e) {
                        log.error("Error saving conversation after stream", e);
                    }
                    emitter.complete();
                }
            );
    }

    public List<Conversation> getConversations(String knowledgeBaseId) {
        return conversationRepository.findByKnowledgeBaseIdOrderByUpdatedAtDesc(knowledgeBaseId);
    }
    
    @Transactional
    public void deleteConversation(String knowledgeBaseId, String conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
            .orElseThrow(() -> new ResourceNotFoundException("Conversation not found: " + conversationId));

        if (!conversation.getKnowledgeBase().getId().equals(knowledgeBaseId)) {
            throw new IllegalArgumentException("Conversation does not belong to this knowledge base");
        }

        conversationRepository.delete(conversation);
    }
}
