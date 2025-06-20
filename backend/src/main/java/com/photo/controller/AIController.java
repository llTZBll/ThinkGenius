package com.photo.controller;

import com.photo.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIController {
    
    @Autowired
    private AIService aiService;
    
    // 生成关键词
    @PostMapping("/keywords")
    public ResponseEntity<Map<String, List<String>>> generateKeywords(@RequestBody Map<String, String> request) {
        try {
            String content = request.get("content");
            List<String> keywords = aiService.generateKeywords(content);
            return ResponseEntity.ok(Map.of("keywords", keywords));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 生成问题
    @PostMapping("/questions")
    public ResponseEntity<Map<String, String>> generateQuestions(@RequestBody Map<String, String> request) {
        try {
            String content = request.get("content");
            String questions = aiService.generateQuestions(content);
            return ResponseEntity.ok(Map.of("questions", questions));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 生成摘要
    @PostMapping("/summary")
    public ResponseEntity<Map<String, String>> generateSummary(@RequestBody Map<String, String> request) {
        try {
            String content = request.get("content");
            String summary = aiService.generateSummary(content);
            return ResponseEntity.ok(Map.of("summary", summary));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 分析内容
    @PostMapping("/analyze")
    public ResponseEntity<Map<String, String>> analyzeContent(@RequestBody Map<String, String> request) {
        try {
            String content = request.get("content");
            String analysis = aiService.analyzeContent(content);
            return ResponseEntity.ok(Map.of("analysis", analysis));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 生成建议
    @PostMapping("/suggestions")
    public ResponseEntity<Map<String, List<String>>> generateSuggestions(@RequestBody Map<String, String> request) {
        try {
            String content = request.get("content");
            List<String> suggestions = aiService.generateSuggestions(content);
            return ResponseEntity.ok(Map.of("suggestions", suggestions));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 聊天接口
    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> request) {
        try {
            String message = request.get("message");
            String response = aiService.chat(message);
            return ResponseEntity.ok(Map.of("response", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 