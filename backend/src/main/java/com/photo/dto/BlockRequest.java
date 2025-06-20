package com.photo.dto;

import com.photo.entity.Block;

public class BlockRequest {
    private String type;
    private Block.Position position;
    private Block.Size size;
    private String content;
    
    public BlockRequest() {}
    
    public BlockRequest(String type, Block.Position position, Block.Size size, String content) {
        this.type = type;
        this.position = position;
        this.size = size;
        this.content = content;
    }
    
    // Getters and Setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public Block.Position getPosition() { return position; }
    public void setPosition(Block.Position position) { this.position = position; }
    
    public Block.Size getSize() { return size; }
    public void setSize(Block.Size size) { this.size = size; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
} 