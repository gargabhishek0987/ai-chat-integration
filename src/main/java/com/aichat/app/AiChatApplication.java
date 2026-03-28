package com.aichat.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AiChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiChatApplication.class, args);
        System.out.println("=========================================");
        System.out.println("  AI Chat Integration API is running!");
        System.out.println("  URL: http://localhost:8080");
        System.out.println("=========================================");
    }
}
