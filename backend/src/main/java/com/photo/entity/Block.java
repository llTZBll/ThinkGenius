package com.photo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "blocks")
public class Block {
    
    @Id
    private String id;
    
    @Field("type")
    private String type; // question、keyword、text
    
    @Field("position")
    private Position position;
    
    @Field("size")
    private Size size;
    
    @Field("content")
    private String content;
    
    @Field("userId")
    private String userId;
    
    @Field("createdAt")
    private LocalDateTime createdAt;
    
    @Field("updatedAt")
    private LocalDateTime updatedAt;
    
    // 内部类：位置
    public static class Position {
        private double x;
        private double y;
        
        public Position() {}
        
        public Position(double x, double y) {
            this.x = x;
            this.y = y;
        }
        
        public double getX() { return x; }
        public void setX(double x) { this.x = x; }
        
        public double getY() { return y; }
        public void setY(double y) { this.y = y; }
    }
    
    // 内部类：大小
    public static class Size {
        private double width;
        private double height;
        
        public Size() {}
        
        public Size(double width, double height) {
            this.width = width;
            this.height = height;
        }
        
        public double getWidth() { return width; }
        public void setWidth(double width) { this.width = width; }
        
        public double getHeight() { return height; }
        public void setHeight(double height) { this.height = height; }
    }
    
    // 构造函数
    public Block() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }
    
    public Size getSize() { return size; }
    public void setSize(Size size) { this.size = size; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
} 