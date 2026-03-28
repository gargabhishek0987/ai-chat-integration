package com.aichat.app.controller;

import com.aichat.app.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * REGISTER
     * POST http://localhost:8080/api/auth/register
     * Body: { "username": "ravi", "password": "password123" }
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Username and password are required.");
            return ResponseEntity.badRequest().body(error);
        }

        Map<String, String> response = authService.register(username, password);
        return ResponseEntity.ok(response);
    }

    /**
     * LOGIN
     * POST http://localhost:8080/api/auth/login
     * Body: { "username": "ravi", "password": "password123" }
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Username and password are required.");
            return ResponseEntity.badRequest().body(error);
        }

        Map<String, String> response = authService.login(username, password);
        return ResponseEntity.ok(response);
    }
}
