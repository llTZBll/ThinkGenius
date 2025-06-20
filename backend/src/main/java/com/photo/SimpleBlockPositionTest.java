package com.photo;

import com.photo.entity.Block;
import com.photo.entity.BlockRelation;
import com.photo.service.impl.BlockPositionServiceImpl;

import java.util.ArrayList;
import java.util.List;

public class SimpleBlockPositionTest {
    
    public static void main(String[] args) {
        System.out.println("=== Block Position Algorithm Test ===");
        
        // 创建位置算法服务
        BlockPositionServiceImpl blockPositionService = new BlockPositionServiceImpl();
        
        // 创建测试数据
        List<Block> existingBlocks = createTestBlocks();
        List<BlockRelation> relations = createTestRelations();
        
        // 测试1: 创建新的question块
        System.out.println("\n--- Test 1: Create new question block ---");
        Block newQuestion = new Block();
        newQuestion.setType("question");
        newQuestion.setContent("What is the main topic?");
        newQuestion.setSize(new Block.Size(300.0, 150.0));
        
        Block.Position questionPosition = blockPositionService.calculateOptimalPosition(newQuestion, existingBlocks, relations);
        System.out.println("New question position: (" + questionPosition.getX() + ", " + questionPosition.getY() + ")");
        
        // 测试2: 创建新的keyword块
        System.out.println("\n--- Test 2: Create new keyword block ---");
        Block newKeyword = new Block();
        newKeyword.setType("keyword");
        newKeyword.setContent("Important concept");
        newKeyword.setSize(new Block.Size(200.0, 100.0));
        
        Block.Position keywordPosition = blockPositionService.calculateOptimalPosition(newKeyword, existingBlocks, relations);
        System.out.println("New keyword position: (" + keywordPosition.getX() + ", " + keywordPosition.getY() + ")");
        
        // 测试3: 创建新的text块
        System.out.println("\n--- Test 3: Create new text block ---");
        Block newText = new Block();
        newText.setType("text");
        newText.setContent("This is a detailed explanation of the concept.");
        newText.setSize(new Block.Size(400.0, 200.0));
        
        Block.Position textPosition = blockPositionService.calculateOptimalPosition(newText, existingBlocks, relations);
        System.out.println("New text position: (" + textPosition.getX() + ", " + textPosition.getY() + ")");
        
        // 测试4: 位置可用性检查
        System.out.println("\n--- Test 4: Position availability check ---");
        Block.Position testPosition = new Block.Position(100.0, 100.0);
        Block.Size testSize = new Block.Size(200.0, 100.0);
        
        boolean isAvailable = blockPositionService.isPositionAvailable(testPosition, testSize, existingBlocks, null);
        System.out.println("Position (100, 100) available: " + isAvailable);
        
        // 测试5: 找到最近可用位置
        System.out.println("\n--- Test 5: Find nearest available position ---");
        Block.Position preferredPosition = new Block.Position(100.0, 100.0);
        Block.Position nearestPosition = blockPositionService.findNearestAvailablePosition(preferredPosition, testSize, existingBlocks, null);
        System.out.println("Nearest available position: (" + nearestPosition.getX() + ", " + nearestPosition.getY() + ")");
        
        // 测试6: 重新布局所有块
        System.out.println("\n--- Test 6: Relayout all blocks ---");
        List<Block> allBlocks = new ArrayList<>(existingBlocks);
        allBlocks.add(newQuestion);
        allBlocks.add(newKeyword);
        allBlocks.add(newText);
        
        List<Block> relayoutedBlocks = blockPositionService.relayoutAllBlocks(allBlocks, relations);
        System.out.println("Relayouted " + relayoutedBlocks.size() + " blocks:");
        for (Block block : relayoutedBlocks) {
            System.out.println("  " + block.getType() + " at (" + block.getPosition().getX() + ", " + block.getPosition().getY() + ")");
        }
        
        // 测试7: 关系位置计算
        System.out.println("\n--- Test 7: Related block position calculation ---");
        Block sourceBlock = existingBlocks.get(0);
        Block targetBlock = new Block();
        targetBlock.setSize(new Block.Size(200.0, 100.0));
        
        Block.Position relatedPosition = blockPositionService.calculateRelatedBlockPosition(sourceBlock, targetBlock, "child");
        System.out.println("Child block position: (" + relatedPosition.getX() + ", " + relatedPosition.getY() + ")");
        
        System.out.println("\n=== Test completed ===");
    }
    
    private static List<Block> createTestBlocks() {
        List<Block> blocks = new ArrayList<>();
        
        // 创建一些测试块
        Block question1 = new Block();
        question1.setId("q1");
        question1.setType("question");
        question1.setContent("What is the main question?");
        question1.setPosition(new Block.Position(500.0, 300.0));
        question1.setSize(new Block.Size(300.0, 150.0));
        blocks.add(question1);
        
        Block keyword1 = new Block();
        keyword1.setId("k1");
        keyword1.setType("keyword");
        keyword1.setContent("Key concept 1");
        keyword1.setPosition(new Block.Position(300.0, 200.0));
        keyword1.setSize(new Block.Size(200.0, 100.0));
        blocks.add(keyword1);
        
        Block keyword2 = new Block();
        keyword2.setId("k2");
        keyword2.setType("keyword");
        keyword2.setContent("Key concept 2");
        keyword2.setPosition(new Block.Position(700.0, 200.0));
        keyword2.setSize(new Block.Size(200.0, 100.0));
        blocks.add(keyword2);
        
        Block text1 = new Block();
        text1.setId("t1");
        text1.setType("text");
        text1.setContent("This is a detailed explanation.");
        text1.setPosition(new Block.Position(100.0, 800.0));
        text1.setSize(new Block.Size(400.0, 200.0));
        blocks.add(text1);
        
        return blocks;
    }
    
    private static List<BlockRelation> createTestRelations() {
        List<BlockRelation> relations = new ArrayList<>();
        
        // 创建一些测试关系
        BlockRelation relation1 = new BlockRelation("q1", "k1", "related", "user1");
        BlockRelation relation2 = new BlockRelation("q1", "k2", "related", "user1");
        BlockRelation relation3 = new BlockRelation("k1", "t1", "child", "user1");
        
        relations.add(relation1);
        relations.add(relation2);
        relations.add(relation3);
        
        return relations;
    }
} 