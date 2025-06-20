package com.photo.service.impl;

import com.photo.entity.Block;
import com.photo.entity.BlockRelation;
import com.photo.service.BlockPositionService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BlockPositionServiceImpl implements BlockPositionService {
    
    // 画布配置
    private static final double CANVAS_WIDTH = 2000.0;
    private static final double CANVAS_HEIGHT = 1500.0;
    private static final double MIN_BLOCK_DISTANCE = 50.0; // 块之间的最小距离
    private static final double GRID_SIZE = 20.0; // 网格大小，用于位置对齐
    
    // 不同类型块的默认大小
    private static final Map<String, Block.Size> DEFAULT_SIZES = Map.of(
        "question", new Block.Size(300.0, 150.0),
        "keyword", new Block.Size(200.0, 100.0),
        "text", new Block.Size(400.0, 200.0)
    );
    
    // 不同类型块的布局策略
    private static final Map<String, LayoutStrategy> LAYOUT_STRATEGIES = Map.of(
        "question", LayoutStrategy.CENTER_FOCUS,
        "keyword", LayoutStrategy.CIRCULAR,
        "text", LayoutStrategy.HIERARCHICAL
    );
    
    @Override
    public Block.Position calculateOptimalPosition(Block newBlock, List<Block> existingBlocks, List<BlockRelation> relations) {
        if (existingBlocks.isEmpty()) {
            // 第一个块，放在画布中心
            return new Block.Position(CANVAS_WIDTH / 2, CANVAS_HEIGHT / 2);
        }
        
        // 获取块的大小
        Block.Size size = newBlock.getSize() != null ? newBlock.getSize() : 
                         DEFAULT_SIZES.getOrDefault(newBlock.getType(), new Block.Size(300.0, 150.0));
        
        // 根据块类型选择布局策略
        LayoutStrategy strategy = LAYOUT_STRATEGIES.getOrDefault(newBlock.getType(), LayoutStrategy.CENTER_FOCUS);
        
        Block.Position position = null;
        switch (strategy) {
            case CENTER_FOCUS:
                position = calculateCenterFocusPosition(newBlock, existingBlocks, relations, size);
                break;
            case CIRCULAR:
                position = calculateCircularPosition(newBlock, existingBlocks, relations, size);
                break;
            case HIERARCHICAL:
                position = calculateHierarchicalPosition(newBlock, existingBlocks, relations, size);
                break;
        }
        
        // 确保位置可用，如果不可用则寻找最近可用位置
        if (!isPositionAvailable(position, size, existingBlocks, null)) {
            position = findNearestAvailablePosition(position, size, existingBlocks, null);
        }
        
        return position;
    }
    
    @Override
    public List<Block> relayoutAllBlocks(List<Block> blocks, List<BlockRelation> relations) {
        if (blocks.isEmpty()) {
            return blocks;
        }
        
        // 按类型分组
        Map<String, List<Block>> blocksByType = blocks.stream()
            .collect(Collectors.groupingBy(Block::getType));
        
        List<Block> relayoutedBlocks = new ArrayList<>();
        
        // 先布局question类型的块（作为中心）
        if (blocksByType.containsKey("question")) {
            List<Block> questionBlocks = blocksByType.get("question");
            relayoutedBlocks.addAll(layoutQuestionBlocks(questionBlocks));
        }
        
        // 布局keyword类型的块（围绕question）
        if (blocksByType.containsKey("keyword")) {
            List<Block> keywordBlocks = blocksByType.get("keyword");
            relayoutedBlocks.addAll(layoutKeywordBlocks(keywordBlocks, relayoutedBlocks, relations));
        }
        
        // 布局text类型的块（在底部）
        if (blocksByType.containsKey("text")) {
            List<Block> textBlocks = blocksByType.get("text");
            relayoutedBlocks.addAll(layoutTextBlocks(textBlocks, relayoutedBlocks));
        }
        
        return relayoutedBlocks;
    }
    
    @Override
    public boolean isPositionAvailable(Block.Position position, Block.Size size, List<Block> existingBlocks, String excludeBlockId) {
        if (position == null || size == null) {
            return false;
        }
        
        // 检查是否超出画布边界
        if (position.getX() < 0 || position.getY() < 0 || 
            position.getX() + size.getWidth() > CANVAS_WIDTH ||
            position.getY() + size.getHeight() > CANVAS_HEIGHT) {
            return false;
        }
        
        // 检查与其他块的重叠
        for (Block block : existingBlocks) {
            if (excludeBlockId != null && excludeBlockId.equals(block.getId())) {
                continue;
            }
            
            if (blocksOverlap(position, size, block.getPosition(), block.getSize())) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public Block.Position findNearestAvailablePosition(Block.Position preferredPosition, Block.Size size, List<Block> existingBlocks, String excludeBlockId) {
        if (isPositionAvailable(preferredPosition, size, existingBlocks, excludeBlockId)) {
            return preferredPosition;
        }
        
        // 螺旋搜索算法
        double angle = 0;
        double radius = MIN_BLOCK_DISTANCE;
        double angleStep = Math.PI / 8; // 45度步长
        double radiusStep = MIN_BLOCK_DISTANCE;
        
        while (radius < Math.max(CANVAS_WIDTH, CANVAS_HEIGHT)) {
            double x = preferredPosition.getX() + radius * Math.cos(angle);
            double y = preferredPosition.getY() + radius * Math.sin(angle);
            
            Block.Position testPosition = new Block.Position(x, y);
            if (isPositionAvailable(testPosition, size, existingBlocks, excludeBlockId)) {
                return testPosition;
            }
            
            angle += angleStep;
            if (angle >= 2 * Math.PI) {
                angle = 0;
                radius += radiusStep;
            }
        }
        
        // 如果找不到合适位置，返回画布边缘
        return new Block.Position(CANVAS_WIDTH - size.getWidth() - 50, CANVAS_HEIGHT - size.getHeight() - 50);
    }
    
    @Override
    public Block.Position calculateRelatedBlockPosition(Block sourceBlock, Block targetBlock, String relationType) {
        Block.Position sourcePos = sourceBlock.getPosition();
        Block.Size sourceSize = sourceBlock.getSize();
        Block.Size targetSize = targetBlock.getSize();
        
        double offsetX = 0, offsetY = 0;
        
        switch (relationType) {
            case "parent":
                // 父块在上方
                offsetY = -(sourceSize.getHeight() + targetSize.getHeight()) / 2 - MIN_BLOCK_DISTANCE;
                break;
            case "child":
                // 子块在下方
                offsetY = (sourceSize.getHeight() + targetSize.getHeight()) / 2 + MIN_BLOCK_DISTANCE;
                break;
            case "related":
                // 相关块在右侧
                offsetX = (sourceSize.getWidth() + targetSize.getWidth()) / 2 + MIN_BLOCK_DISTANCE;
                break;
            case "similar":
                // 相似块在左侧
                offsetX = -(sourceSize.getWidth() + targetSize.getWidth()) / 2 - MIN_BLOCK_DISTANCE;
                break;
        }
        
        return new Block.Position(sourcePos.getX() + offsetX, sourcePos.getY() + offsetY);
    }
    
    // 私有辅助方法
    
    private Block.Position calculateCenterFocusPosition(Block newBlock, List<Block> existingBlocks, List<BlockRelation> relations, Block.Size size) {
        // 找到画布中心
        double centerX = CANVAS_WIDTH / 2;
        double centerY = CANVAS_HEIGHT / 2;
        
        // 如果有现有的question块，围绕它们布局
        List<Block> questionBlocks = existingBlocks.stream()
            .filter(b -> "question".equals(b.getType()))
            .collect(Collectors.toList());
        
        if (!questionBlocks.isEmpty()) {
            // 计算现有question块的中心
            double avgX = questionBlocks.stream().mapToDouble(b -> b.getPosition().getX()).average().orElse(centerX);
            double avgY = questionBlocks.stream().mapToDouble(b -> b.getPosition().getY()).average().orElse(centerY);
            
            // 在中心附近找一个位置
            return new Block.Position(avgX, avgY + questionBlocks.size() * (size.getHeight() + MIN_BLOCK_DISTANCE));
        }
        
        return new Block.Position(centerX, centerY);
    }
    
    private Block.Position calculateCircularPosition(Block newBlock, List<Block> existingBlocks, List<BlockRelation> relations, Block.Size size) {
        // 找到相关的question块
        List<Block> relatedQuestions = findRelatedQuestions(newBlock, existingBlocks, relations);
        
        if (!relatedQuestions.isEmpty()) {
            // 围绕相关question块布局
            Block centerBlock = relatedQuestions.get(0);
            double centerX = centerBlock.getPosition().getX();
            double centerY = centerBlock.getPosition().getY();
            
            // 计算圆形布局的位置
            int keywordCount = (int) existingBlocks.stream().filter(b -> "keyword".equals(b.getType())).count();
            double angle = (2 * Math.PI * keywordCount) / 8; // 最多8个keyword围绕
            double radius = 200.0; // 圆形半径
            
            return new Block.Position(
                centerX + radius * Math.cos(angle),
                centerY + radius * Math.sin(angle)
            );
        }
        
        // 如果没有相关question，使用默认位置
        return new Block.Position(100.0, 100.0 + existingBlocks.size() * (size.getHeight() + MIN_BLOCK_DISTANCE));
    }
    
    private Block.Position calculateHierarchicalPosition(Block newBlock, List<Block> existingBlocks, List<BlockRelation> relations, Block.Size size) {
        // text块通常在底部，按层次排列
        double baseY = CANVAS_HEIGHT - 200.0; // 距离底部200px
        int textCount = (int) existingBlocks.stream().filter(b -> "text".equals(b.getType())).count();
        
        return new Block.Position(50.0 + textCount * (size.getWidth() + MIN_BLOCK_DISTANCE), baseY);
    }
    
    private List<Block> layoutQuestionBlocks(List<Block> questionBlocks) {
        List<Block> layoutedBlocks = new ArrayList<>();
        double centerX = CANVAS_WIDTH / 2;
        double centerY = CANVAS_HEIGHT / 2;
        
        for (int i = 0; i < questionBlocks.size(); i++) {
            Block block = questionBlocks.get(i);
            double x = centerX - (questionBlocks.size() - 1) * 150.0 / 2 + i * 150.0;
            double y = centerY;
            
            block.setPosition(new Block.Position(x, y));
            layoutedBlocks.add(block);
        }
        
        return layoutedBlocks;
    }
    
    private List<Block> layoutKeywordBlocks(List<Block> keywordBlocks, List<Block> existingBlocks, List<BlockRelation> relations) {
        List<Block> layoutedBlocks = new ArrayList<>();
        
        for (Block keywordBlock : keywordBlocks) {
            // 找到相关的question块
            List<Block> relatedQuestions = findRelatedQuestions(keywordBlock, existingBlocks, relations);
            
            if (!relatedQuestions.isEmpty()) {
                // 围绕相关question布局
                Block centerBlock = relatedQuestions.get(0);
                Block.Position position = calculateCircularPosition(keywordBlock, existingBlocks, relations, keywordBlock.getSize());
                keywordBlock.setPosition(position);
            } else {
                // 使用默认位置
                double x = 100.0 + layoutedBlocks.size() * 250.0;
                double y = 100.0;
                keywordBlock.setPosition(new Block.Position(x, y));
            }
            
            layoutedBlocks.add(keywordBlock);
        }
        
        return layoutedBlocks;
    }
    
    private List<Block> layoutTextBlocks(List<Block> textBlocks, List<Block> existingBlocks) {
        List<Block> layoutedBlocks = new ArrayList<>();
        double baseY = CANVAS_HEIGHT - 200.0;
        
        for (int i = 0; i < textBlocks.size(); i++) {
            Block block = textBlocks.get(i);
            double x = 50.0 + i * 450.0; // 每个text块间隔450px
            block.setPosition(new Block.Position(x, baseY));
            layoutedBlocks.add(block);
        }
        
        return layoutedBlocks;
    }
    
    private List<Block> findRelatedQuestions(Block block, List<Block> existingBlocks, List<BlockRelation> relations) {
        // 添加 null 检查
        if (block == null || block.getId() == null) {
            return new ArrayList<>();
        }
        
        return relations.stream()
            .filter(r -> block.getId().equals(r.getSourceBlockId()) || block.getId().equals(r.getTargetBlockId()))
            .map(r -> {
                String relatedId = r.getSourceBlockId().equals(block.getId()) ? r.getTargetBlockId() : r.getSourceBlockId();
                return existingBlocks.stream()
                    .filter(b -> b != null && b.getId() != null && b.getId().equals(relatedId))
                    .findFirst()
                    .orElse(null);
            })
            .filter(Objects::nonNull)
            .filter(b -> "question".equals(b.getType()))
            .collect(Collectors.toList());
    }
    
    private boolean blocksOverlap(Block.Position pos1, Block.Size size1, Block.Position pos2, Block.Size size2) {
        if (pos1 == null || pos2 == null || size1 == null || size2 == null) {
            return false;
        }
        
        return pos1.getX() < pos2.getX() + size2.getWidth() &&
               pos1.getX() + size1.getWidth() > pos2.getX() &&
               pos1.getY() < pos2.getY() + size2.getHeight() &&
               pos1.getY() + size1.getHeight() > pos2.getY();
    }
    
    // 布局策略枚举
    private enum LayoutStrategy {
        CENTER_FOCUS,    // 中心聚焦布局
        CIRCULAR,        // 圆形布局
        HIERARCHICAL     // 层次布局
    }
} 