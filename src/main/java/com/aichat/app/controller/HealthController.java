package com.aichat.app.controller;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class HealthController {

    /**
     * HEALTH CHECK - No auth needed
     * GET http://localhost:8080/api/health
     */
    @GetMapping("/api/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "AI Chat Integration API is running!");
        response.put("version", "1.0.0");
        return response;
    }
}
