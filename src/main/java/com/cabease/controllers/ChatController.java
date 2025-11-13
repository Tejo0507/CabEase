package com.cabease.controllers;
import com.cabease.services.GeminiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController
@RequestMapping("/api/chat")
@Slf4j
@CrossOrigin(origins = "*")
public class ChatController {
    @Autowired
    private GeminiService geminiService;
    @PostMapping
    public ResponseEntity<Map<String, Object>> chat(@RequestBody Map<String, Object> requestBody) {
        try {
            String message = (String) requestBody.get("message");
            Map<String, Object> context = (Map<String, Object>) requestBody.get("context");
            log.info("Received chat message: {}", message);
            log.info("Context: {}", context);
            String response = geminiService.chat(message, context);
            Map<String, Object> responseBody = Map.of(
                "response", response,
                "success", true
            );
            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            log.error("Error in chat", e);
            Map<String, Object> errorResponse = Map.of(
                "response", "Sorry, I'm having trouble responding right now. Please try again later.",
                "success", false,
                "error", e.getMessage()
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}
