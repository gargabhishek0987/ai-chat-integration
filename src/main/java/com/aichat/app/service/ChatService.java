package com.aichat.app.service;

import com.aichat.app.model.ChatHistory;
import com.aichat.app.repository.ChatHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {

    @Autowired
    private GroqAIService groqAIService;

    @Autowired
    private ChatHistoryRepository chatHistoryRepository;

    public ChatHistory chat(String username, String userMessage) {
        // Step 1: Call Groq AI
        String aiResponse = groqAIService.chat(userMessage);

        // Step 2: Save to MySQL
        ChatHistory history = new ChatHistory();
        history.setUsername(username);
        history.setUserMessage(userMessage);
        history.setAiResponse(aiResponse);
        history.setCreatedAt(LocalDateTime.now());

        return chatHistoryRepository.save(history);
    }

    public List<ChatHistory> getHistory(String username) {
        return chatHistoryRepository.findByUsernameOrderByCreatedAtDesc(username);
    }

    public List<ChatHistory> getAllHistory() {
        return chatHistoryRepository.findAll();
    }
}
