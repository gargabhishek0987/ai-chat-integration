package com.aichat.app.controller;

import com.aichat.app.model.ChatHistory;
import com.aichat.app.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private ChatService chatService;

    /**
     * SEND MESSAGE TO AI
     * POST http://localhost:8080/api/chat
     * Header: Authorization: Bearer <token>
     * Body: { "message": "What is system integration?" }
     */
    @PostMapping
    public ResponseEntity<?> chat(
            @RequestBody Map<String, String> request,
            Authentication authentication) {

        String message = request.get("message");

        if (message == null || message.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Message cannot be empty.");
            return ResponseEntity.badRequest().body(error);
        }

        String username = authentication.getName();
        ChatHistory result = chatService.chat(username, message);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("id", result.getId());
        response.put("username", result.getUsername());
        response.put("yourMessage", result.getUserMessage());
        response.put("aiResponse", result.getAiResponse());
        response.put("timestamp", result.getCreatedAt().toString());

        return ResponseEntity.ok(response);
    }

    /**
     * GET CHAT HISTORY
     * GET http://localhost:8080/api/chat/history
     * Header: Authorization: Bearer <token>
     */
    @GetMapping("/history")
    public ResponseEntity<List<ChatHistory>> getHistory(Authentication authentication) {
        String username = authentication.getName();
        List<ChatHistory> history = chatService.getHistory(username);
        return ResponseEntity.ok(history);
    }

    /**
     * GET ALL CHATS
     * GET http://localhost:8080/api/chat/all
     * Header: Authorization: Bearer <token>
     */
    @GetMapping("/all")
    public ResponseEntity<List<ChatHistory>> getAllChats() {
        List<ChatHistory> all = chatService.getAllHistory();
        return ResponseEntity.ok(all);
    }
}
