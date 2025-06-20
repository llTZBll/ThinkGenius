package com.photo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "block_relations")
public class BlockRelation {
    
    @Id
    private String id;
    
    @Field("sourceBlockId")
    private String sourceBlockId;
    
    @Field("targetBlockId")
    private String targetBlockId;
    
    @Field("relationType")
    private String relationType; // "related", "parent", "child", "similar"
    
    @Field("userId")
    private String userId;
    
    @Field("createdAt")
    private LocalDateTime createdAt;
    
    public BlockRelation() {
        this.createdAt = LocalDateTime.now();
    }
    
    public BlockRelation(String sourceBlockId, String targetBlockId, String relationType, String userId) {
        this.sourceBlockId = sourceBlockId;
        this.targetBlockId = targetBlockId;
        this.relationType = relationType;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getSourceBlockId() { return sourceBlockId; }
    public void setSourceBlockId(String sourceBlockId) { this.sourceBlockId = sourceBlockId; }
    
    public String getTargetBlockId() { return targetBlockId; }
    public void setTargetBlockId(String targetBlockId) { this.targetBlockId = targetBlockId; }
    
    public String getRelationType() { return relationType; }
    public void setRelationType(String relationType) { this.relationType = relationType; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
} 