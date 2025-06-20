package com.photo;

import com.photo.entity.Block;
import com.photo.entity.BlockRelation;
import com.photo.service.BlockRelationService;
import com.photo.service.BlockService;
import com.photo.service.impl.BlockRelationServiceImpl;
import com.photo.service.impl.BlockServiceImpl;
import com.photo.repository.BlockRepository;
import com.photo.repository.BlockRelationRepository;
import com.photo.dto.BlockRequest;

import java.util.ArrayList;
import java.util.List;

public class BlockRelationTest {
    
    public static void main(String[] args) {
        System.out.println("=== Block Relation Management Test ===");
        
        // 注意：这是一个简化的测试，实际运行时需要Spring容器
        // 这里主要用于验证逻辑正确性
        
        // 模拟测试数据
        testBlockRelationLogic();
        
        System.out.println("\n=== Test completed ===");
    }
    
    private static void testBlockRelationLogic() {
        System.out.println("\n--- Testing Block Relation Logic ---");
        
        // 模拟创建测试块
        List<Block> testBlocks = createTestBlocks();
        List<BlockRelation> testRelations = createTestRelations();
        
        // 测试1: 关系创建逻辑
        System.out.println("Test 1: Relation creation logic");
        BlockRelation relation = new BlockRelation("block1", "block2", "related", "user1");
        System.out.println("Created relation: " + relation.getSourceBlockId() + " -> " + relation.getTargetBlockId() + " (" + relation.getRelationType() + ")");
        
        // 测试2: 关系查询逻辑
        System.out.println("\nTest 2: Relation query logic");
        List<BlockRelation> block1Relations = testRelations.stream()
            .filter(r -> r.getSourceBlockId().equals("block1") || r.getTargetBlockId().equals("block1"))
            .toList();
        System.out.println("Block1 has " + block1Relations.size() + " relations");
        
        // 测试3: 关系类型统计
        System.out.println("\nTest 3: Relation type statistics");
        long relatedCount = testRelations.stream().filter(r -> "related".equals(r.getRelationType())).count();
        long parentCount = testRelations.stream().filter(r -> "parent".equals(r.getRelationType())).count();
        long childCount = testRelations.stream().filter(r -> "child".equals(r.getRelationType())).count();
        long similarCount = testRelations.stream().filter(r -> "similar".equals(r.getRelationType())).count();
        
        System.out.println("Related relations: " + relatedCount);
        System.out.println("Parent relations: " + parentCount);
        System.out.println("Child relations: " + childCount);
        System.out.println("Similar relations: " + similarCount);
        
        // 测试4: 关系存在性检查
        System.out.println("\nTest 4: Relation existence check");
        boolean exists1 = testRelations.stream()
            .anyMatch(r -> (r.getSourceBlockId().equals("block1") && r.getTargetBlockId().equals("block2")) ||
                          (r.getSourceBlockId().equals("block2") && r.getTargetBlockId().equals("block1")));
        System.out.println("Relation between block1 and block2 exists: " + exists1);
        
        boolean exists2 = testRelations.stream()
            .anyMatch(r -> (r.getSourceBlockId().equals("block1") && r.getTargetBlockId().equals("block3")) ||
                          (r.getSourceBlockId().equals("block3") && r.getTargetBlockId().equals("block1")));
        System.out.println("Relation between block1 and block3 exists: " + exists2);
        
        // 测试5: 块删除时的关系清理
        System.out.println("\nTest 5: Relation cleanup on block deletion");
        String blockToDelete = "block1";
        List<BlockRelation> relationsToDelete = testRelations.stream()
            .filter(r -> r.getSourceBlockId().equals(blockToDelete) || r.getTargetBlockId().equals(blockToDelete))
            .toList();
        System.out.println("When deleting " + blockToDelete + ", " + relationsToDelete.size() + " relations should be deleted");
        
        // 测试6: 关系类型验证
        System.out.println("\nTest 6: Relation type validation");
        List<String> validTypes = List.of("related", "parent", "child", "similar");
        String testType = "related";
        boolean isValidType = validTypes.contains(testType);
        System.out.println("Relation type '" + testType + "' is valid: " + isValidType);
        
        // 测试7: 双向关系处理
        System.out.println("\nTest 7: Bidirectional relation handling");
        BlockRelation forwardRelation = testRelations.stream()
            .filter(r -> r.getSourceBlockId().equals("block1") && r.getTargetBlockId().equals("block2"))
            .findFirst()
            .orElse(null);
        
        BlockRelation reverseRelation = testRelations.stream()
            .filter(r -> r.getSourceBlockId().equals("block2") && r.getTargetBlockId().equals("block1"))
            .findFirst()
            .orElse(null);
        
        System.out.println("Forward relation exists: " + (forwardRelation != null));
        System.out.println("Reverse relation exists: " + (reverseRelation != null));
        
        // 测试8: 用户隔离
        System.out.println("\nTest 8: User isolation");
        List<BlockRelation> user1Relations = testRelations.stream()
            .filter(r -> "user1".equals(r.getUserId()))
            .toList();
        List<BlockRelation> user2Relations = testRelations.stream()
            .filter(r -> "user2".equals(r.getUserId()))
            .toList();
        
        System.out.println("User1 has " + user1Relations.size() + " relations");
        System.out.println("User2 has " + user2Relations.size() + " relations");
    }
    
    private static List<Block> createTestBlocks() {
        List<Block> blocks = new ArrayList<>();
        
        // 创建测试块
        Block block1 = new Block();
        block1.setId("block1");
        block1.setType("question");
        block1.setContent("What is the main topic?");
        block1.setUserId("user1");
        blocks.add(block1);
        
        Block block2 = new Block();
        block2.setId("block2");
        block2.setType("keyword");
        block2.setContent("Important concept");
        block2.setUserId("user1");
        blocks.add(block2);
        
        Block block3 = new Block();
        block3.setId("block3");
        block3.setType("text");
        block3.setContent("Detailed explanation");
        block3.setUserId("user1");
        blocks.add(block3);
        
        Block block4 = new Block();
        block4.setId("block4");
        block4.setType("question");
        block4.setContent("Another question");
        block4.setUserId("user2");
        blocks.add(block4);
        
        return blocks;
    }
    
    private static List<BlockRelation> createTestRelations() {
        List<BlockRelation> relations = new ArrayList<>();
        
        // 创建测试关系
        relations.add(new BlockRelation("block1", "block2", "related", "user1"));
        relations.add(new BlockRelation("block1", "block3", "child", "user1"));
        relations.add(new BlockRelation("block2", "block3", "parent", "user1"));
        relations.add(new BlockRelation("block4", "block1", "similar", "user2"));
        
        return relations;
    }
} 