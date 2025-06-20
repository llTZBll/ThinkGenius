package com.photo.service;

import java.util.List;

public interface AIService {
    
    // 生成关键词
    List<String> generateKeywords(String content);
    
    // 生成问题
    String generateQuestions(String content);
    
    // 生成文本摘要
    String generateSummary(String content);
    
    // 分析文本内容
    String analyzeContent(String content);
    
    // 生成相关建议
    List<String> generateSuggestions(String content);
    
    // 聊天接口
    String chat(String message);
} 