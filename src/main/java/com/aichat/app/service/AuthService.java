package com.aichat.app.service;

import com.aichat.app.model.User;
import com.aichat.app.repository.UserRepository;
import com.aichat.app.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    public Map<String, String> register(String username, String password) {
        Map<String, String> response = new HashMap<>();

        if (userRepository.existsByUsername(username)) {
            response.put("status", "error");
            response.put("message", "Username '" + username + "' already exists.");
            return response;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");
        userRepository.save(user);

        response.put("status", "success");
        response.put("message", "User registered successfully! You can now login.");
        response.put("username", username);
        return response;
    }

    public Map<String, String> login(String username, String password) {
        Map<String, String> response = new HashMap<>();

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            String token = jwtUtil.generateToken(username);

            response.put("status", "success");
            response.put("message", "Login successful!");
            response.put("token", token);
            response.put("username", username);
            response.put("tokenType", "Bearer");
            response.put("note", "Copy this token and use it in Postman as Bearer Token");
            return response;

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Invalid username or password.");
            return response;
        }
    }
}
