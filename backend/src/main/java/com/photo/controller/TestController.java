package com.photo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/test")
public class TestController {
    
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);
    
    public TestController() {
        logger.info("TestController initialized!");
    }

    @GetMapping("/ping")
    public String ping() {
        logger.info("ping endpoint called");
        return "TestController is working!";
    }
    
    @GetMapping("/auth-test")
    public String authTest() {
        return "Auth endpoints should be accessible";
    }
    
    @GetMapping("/blocks-test")
    public String blocksTest() {
        return "Blocks endpoints should be accessible";
    }
    
    @GetMapping("/ai-test")
    public String aiTest() {
        return "AI endpoints should be accessible";
    }
    
    @GetMapping("/endpoints")
    public String listEndpoints() {
        return "TestController endpoints are working!";
    }
    
    @GetMapping("/simple")
    public String simple() {
        return "Simple test endpoint";
    }
} 