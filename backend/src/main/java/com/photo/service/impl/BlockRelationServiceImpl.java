package com.photo.service.impl;

import com.photo.entity.BlockRelation;
import com.photo.repository.BlockRelationRepository;
import com.photo.service.BlockRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class BlockRelationServiceImpl implements BlockRelationService {
    
    @Autowired
    private BlockRelationRepository blockRelationRepository;
    
    // 内存存储作为MongoDB的替代方案
    private final ConcurrentHashMap<String, BlockRelation> memoryStorage = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);
    
    @Override
    public BlockRelation createRelation(String sourceBlockId, String targetBlockId, String relationType, String userId) {
        // 检查是否已存在相同的关系
        if (relationExists(sourceBlockId, targetBlockId, userId)) {
            throw new RuntimeException("Relation already exists between these blocks");
        }
        
        // 创建关系对象
        BlockRelation relation = new BlockRelation(sourceBlockId, targetBlockId, relationType, userId);
        relation.setId("relation-" + idCounter.getAndIncrement());
        
        // 存储到内存中
        memoryStorage.put(relation.getId(), relation);
        
        return relation;
    }
    
    @Override
    public BlockRelation getRelationById(String id, String userId) {
        BlockRelation relation = memoryStorage.get(id);
        if (relation != null && relation.getUserId().equals(userId)) {
            return relation;
        }
        throw new RuntimeException("Relation not found or access denied");
    }
    
    @Override
    public List<BlockRelation> getAllRelationsByUserId(String userId) {
        return memoryStorage.values().stream()
            .filter(relation -> relation.getUserId().equals(userId))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<BlockRelation> getRelationsByBlockId(String blockId, String userId) {
        return memoryStorage.values().stream()
            .filter(relation -> relation.getUserId().equals(userId))
            .filter(relation -> relation.getSourceBlockId().equals(blockId) || relation.getTargetBlockId().equals(blockId))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<BlockRelation> getRelationsByType(String userId, String relationType) {
        return memoryStorage.values().stream()
            .filter(relation -> relation.getUserId().equals(userId))
            .filter(relation -> relation.getRelationType().equals(relationType))
            .collect(Collectors.toList());
    }
    
    @Override
    public BlockRelation updateRelationType(String id, String relationType, String userId) {
        BlockRelation relation = getRelationById(id, userId);
        relation.setRelationType(relationType);
        memoryStorage.put(id, relation);
        return relation;
    }
    
    @Override
    public void deleteRelation(String id, String userId) {
        BlockRelation relation = getRelationById(id, userId);
        memoryStorage.remove(id);
    }
    
    @Override
    public void deleteRelationsByBlockId(String blockId, String userId) {
        // 删除以该块为源或目标的所有关系
        List<String> idsToRemove = memoryStorage.values().stream()
            .filter(relation -> relation.getUserId().equals(userId))
            .filter(relation -> relation.getSourceBlockId().equals(blockId) || relation.getTargetBlockId().equals(blockId))
            .map(BlockRelation::getId)
            .collect(Collectors.toList());
        
        idsToRemove.forEach(memoryStorage::remove);
    }
    
    @Override
    public void deleteAllRelationsByUserId(String userId) {
        List<String> idsToRemove = memoryStorage.values().stream()
            .filter(relation -> relation.getUserId().equals(userId))
            .map(BlockRelation::getId)
            .collect(Collectors.toList());
        
        idsToRemove.forEach(memoryStorage::remove);
    }
    
    @Override
    public boolean relationExists(String sourceBlockId, String targetBlockId, String userId) {
        return memoryStorage.values().stream()
            .filter(relation -> relation.getUserId().equals(userId))
            .anyMatch(relation -> 
                (relation.getSourceBlockId().equals(sourceBlockId) && relation.getTargetBlockId().equals(targetBlockId)) ||
                (relation.getSourceBlockId().equals(targetBlockId) && relation.getTargetBlockId().equals(sourceBlockId))
            );
    }
    
    @Override
    public BlockRelation getRelationBetweenBlocks(String sourceBlockId, String targetBlockId, String userId) {
        return memoryStorage.values().stream()
            .filter(relation -> relation.getUserId().equals(userId))
            .filter(relation -> 
                (relation.getSourceBlockId().equals(sourceBlockId) && relation.getTargetBlockId().equals(targetBlockId)) ||
                (relation.getSourceBlockId().equals(targetBlockId) && relation.getTargetBlockId().equals(sourceBlockId))
            )
            .findFirst()
            .orElse(null);
    }
} 