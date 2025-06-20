package com.photo.service.impl;

import com.photo.service.AIService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Service
public class AIServiceImpl implements AIService {
    
    @Value("${baidu.qianfan.api-key}")
    private String apiKey;
    
    @Value("${baidu.qianfan.model}")
    private String model;
    
    @Autowired
    private RestTemplate restTemplate;
    
    private final String API_URL = "https://qianfan.baidubce.com/v2/chat/completions";
    
    @Override
    public List<String> generateKeywords(String content) {
        String prompt = "请从以下文本中提取3-5个有意义的关键词，使用逗号分隔，且不要返回单个字或无意义词。\n" +
                "示例：\n输入：我想学习python？\n输出：python, 面向对象, 编程基础, 语法, 数据分析\n" +
                "输入：" + content + "\n输出：";
        String aiResult = callWenxinAPI(prompt);
        // 用逗号、换行、分号等分割，过滤掉单字、空白，限制3-5个
        List<String> keywords = new ArrayList<>();
        if (aiResult != null && !aiResult.isEmpty()) {
            String[] parts = aiResult.split("[，,;；\n]");
            for (String part : parts) {
                String kw = part.trim();
                if (kw.length() > 1 && keywords.size() < 5) {
                    keywords.add(kw);
                }
                if (keywords.size() >= 5) break;
            }
        }
        // 至少返回3个
        while (keywords.size() > 5) keywords.remove(keywords.size() - 1);
        while (keywords.size() < 3) keywords.add("");
        return keywords;
    }
    
    @Override
    public String generateQuestions(String content) {
        String prompt = "基于以下文本内容，生成3-5个相关问题：\n" + content;
        return callWenxinAPI(prompt);
    }
    
    @Override
    public String generateSummary(String content) {
        String prompt = "请对以下文本进行摘要，控制在100字以内：\n" + content;
        return callWenxinAPI(prompt);
    }
    
    @Override
    public String analyzeContent(String content) {
        String prompt = "请分析以下文本的内容特点、主题和结构：\n" + content;
        return callWenxinAPI(prompt);
    }
    
    @Override
    public List<String> generateSuggestions(String content) {
        String prompt = "基于以下文本内容，提供3-5个改进或扩展建议：\n" + content;
        String response = callWenxinAPI(prompt);
        
        // 简单的响应解析，将建议分割成列表
        List<String> suggestions = new ArrayList<>();
        if (response != null && !response.isEmpty()) {
            String[] parts = response.split("\\d+\\.");
            for (String part : parts) {
                String trimmed = part.trim();
                if (!trimmed.isEmpty()) {
                    suggestions.add(trimmed);
                }
            }
        }
        
        return suggestions;
    }
    
    @Override
    public String chat(String message) {
        return callWenxinAPI(message);
    }
    
    private String callWenxinAPI(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", Arrays.asList(
                Map.of("role", "user", "content", prompt)
            ));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                // 正确解析百度API v2 chat/completions返回结构
                if (body.containsKey("choices")) {
                    Object choicesObj = body.get("choices");
                    if (choicesObj instanceof List && !((List<?>) choicesObj).isEmpty()) {
                        Object firstChoice = ((List<?>) choicesObj).get(0);
                        if (firstChoice instanceof Map) {
                            Map<?, ?> choiceMap = (Map<?, ?>) firstChoice;
                            Object messageObj = choiceMap.get("message");
                            if (messageObj instanceof Map) {
                                Object contentObj = ((Map<?, ?>) messageObj).get("content");
                                if (contentObj instanceof String) {
                                    return (String) contentObj;
                                }
                            }
                        }
                    }
                }
            }

            return "AI服务暂时不可用";
        } catch (Exception e) {
            e.printStackTrace();
            return "AI服务调用失败: " + e.getMessage();
        }
    }
} 