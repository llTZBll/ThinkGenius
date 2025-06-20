package com.photo.service.impl;

import com.photo.dto.BlockRequest;
import com.photo.entity.Block;
import com.photo.entity.BlockRelation;
import com.photo.repository.BlockRepository;
import com.photo.repository.BlockRelationRepository;
import com.photo.service.BlockPositionService;
import com.photo.service.BlockRelationService;
import com.photo.service.BlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class BlockServiceImpl implements BlockService {
    
    @Autowired
    private BlockRepository blockRepository;
    
    @Autowired
    private BlockRelationRepository blockRelationRepository;
    
    @Autowired
    private BlockPositionService blockPositionService;
    
    @Autowired
    private BlockRelationService blockRelationService;
    
    // 内存存储作为MongoDB的替代方案
    private final ConcurrentHashMap<String, Block> memoryStorage = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);
    
    @Override
    public Block createBlock(BlockRequest request, String userId) {
        Block block = new Block();
        block.setId("block-" + idCounter.getAndIncrement());
        block.setType(request.getType());
        block.setPosition(request.getPosition());
        block.setSize(request.getSize());
        block.setContent(request.getContent());
        block.setUserId(userId);
        block.setCreatedAt(LocalDateTime.now());
        block.setUpdatedAt(LocalDateTime.now());
        
        memoryStorage.put(block.getId(), block);
        return block;
    }
    
    @Override
    public Block createBlockWithAutoPosition(BlockRequest request, String userId) {
        // 获取用户现有的所有块
        List<Block> existingBlocks = getAllBlocksByUserId(userId);
        
        // 获取块之间的关系
        List<BlockRelation> relations = blockRelationRepository.findByUserId(userId);
        
        // 创建新块（暂时不设置位置）
        Block block = new Block();
        block.setId("block-" + idCounter.getAndIncrement());
        block.setType(request.getType());
        block.setSize(request.getSize());
        block.setContent(request.getContent());
        block.setUserId(userId);
        block.setCreatedAt(LocalDateTime.now());
        block.setUpdatedAt(LocalDateTime.now());
        
        // 计算最佳位置
        Block.Position optimalPosition = blockPositionService.calculateOptimalPosition(block, existingBlocks, relations);
        block.setPosition(optimalPosition);
        
        // 保存块
        memoryStorage.put(block.getId(), block);
        return block;
    }
    
    @Override
    public Block getBlockById(String id, String userId) {
        Block block = memoryStorage.get(id);
        if (block != null && block.getUserId().equals(userId)) {
            return block;
        }
        throw new RuntimeException("Block not found or access denied");
    }
    
    @Override
    public List<Block> getAllBlocksByUserId(String userId) {
        return memoryStorage.values().stream()
            .filter(block -> block.getUserId().equals(userId))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Block> getBlocksByType(String userId, String type) {
        return memoryStorage.values().stream()
            .filter(block -> block.getUserId().equals(userId))
            .filter(block -> block.getType().equals(type))
            .collect(Collectors.toList());
    }
    
    @Override
    public Block updateBlock(String id, BlockRequest request, String userId) {
        Block existingBlock = getBlockById(id, userId);
        
        existingBlock.setType(request.getType());
        existingBlock.setPosition(request.getPosition());
        existingBlock.setSize(request.getSize());
        existingBlock.setContent(request.getContent());
        existingBlock.setUpdatedAt(LocalDateTime.now());
        
        memoryStorage.put(id, existingBlock);
        return existingBlock;
    }
    
    @Override
    public Block updateBlockPosition(String id, Block.Position position, String userId) {
        Block existingBlock = getBlockById(id, userId);
        
        existingBlock.setPosition(position);
        existingBlock.setUpdatedAt(LocalDateTime.now());
        
        memoryStorage.put(id, existingBlock);
        return existingBlock;
    }
    
    @Override
    public void deleteBlock(String id, String userId) {
        Block block = getBlockById(id, userId);
        
        // 删除与块相关的所有关系
        blockRelationService.deleteRelationsByBlockId(id, userId);
        
        // 删除块
        memoryStorage.remove(id);
    }
    
    @Override
    public void deleteAllBlocksByUserId(String userId) {
        // 删除用户的所有关系
        blockRelationService.deleteAllRelationsByUserId(userId);
        
        // 删除用户的所有块
        List<String> idsToRemove = memoryStorage.values().stream()
            .filter(block -> block.getUserId().equals(userId))
            .map(Block::getId)
            .collect(Collectors.toList());
        
        idsToRemove.forEach(memoryStorage::remove);
    }
    
    @Override
    public List<Block> relayoutUserBlocks(String userId) {
        // 获取用户的所有块
        List<Block> blocks = getAllBlocksByUserId(userId);
        
        // 获取块之间的关系
        List<BlockRelation> relations = blockRelationRepository.findByUserId(userId);
        
        // 重新布局所有块
        List<Block> relayoutedBlocks = blockPositionService.relayoutAllBlocks(blocks, relations);
        
        // 保存重新布局的块
        for (Block block : relayoutedBlocks) {
            block.setUpdatedAt(LocalDateTime.now());
            memoryStorage.put(block.getId(), block);
        }
        
        return relayoutedBlocks;
    }
    
    @Override
    public boolean isPositionAvailable(Block.Position position, Block.Size size, String userId, String excludeBlockId) {
        List<Block> existingBlocks = getAllBlocksByUserId(userId);
        return blockPositionService.isPositionAvailable(position, size, existingBlocks, excludeBlockId);
    }
    
    @Override
    public Block.Position findNearestAvailablePosition(Block.Position preferredPosition, Block.Size size, String userId, String excludeBlockId) {
        List<Block> existingBlocks = getAllBlocksByUserId(userId);
        return blockPositionService.findNearestAvailablePosition(preferredPosition, size, existingBlocks, excludeBlockId);
    }
} 