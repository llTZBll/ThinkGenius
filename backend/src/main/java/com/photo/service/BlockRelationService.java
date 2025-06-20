package com.photo.service;

import com.photo.entity.BlockRelation;

import java.util.List;

public interface BlockRelationService {
    
    /**
     * 创建块关系
     * @param sourceBlockId 源块ID
     * @param targetBlockId 目标块ID
     * @param relationType 关系类型
     * @param userId 用户ID
     * @return 创建的关系
     */
    BlockRelation createRelation(String sourceBlockId, String targetBlockId, String relationType, String userId);
    
    /**
     * 根据ID获取关系
     * @param id 关系ID
     * @param userId 用户ID
     * @return 关系对象
     */
    BlockRelation getRelationById(String id, String userId);
    
    /**
     * 获取用户的所有关系
     * @param userId 用户ID
     * @return 关系列表
     */
    List<BlockRelation> getAllRelationsByUserId(String userId);
    
    /**
     * 获取与指定块相关的所有关系
     * @param blockId 块ID
     * @param userId 用户ID
     * @return 关系列表
     */
    List<BlockRelation> getRelationsByBlockId(String blockId, String userId);
    
    /**
     * 根据关系类型获取关系
     * @param userId 用户ID
     * @param relationType 关系类型
     * @return 关系列表
     */
    List<BlockRelation> getRelationsByType(String userId, String relationType);
    
    /**
     * 更新关系类型
     * @param id 关系ID
     * @param relationType 新的关系类型
     * @param userId 用户ID
     * @return 更新后的关系
     */
    BlockRelation updateRelationType(String id, String relationType, String userId);
    
    /**
     * 删除关系
     * @param id 关系ID
     * @param userId 用户ID
     */
    void deleteRelation(String id, String userId);
    
    /**
     * 删除与指定块相关的所有关系
     * @param blockId 块ID
     * @param userId 用户ID
     */
    void deleteRelationsByBlockId(String blockId, String userId);
    
    /**
     * 删除用户的所有关系
     * @param userId 用户ID
     */
    void deleteAllRelationsByUserId(String userId);
    
    /**
     * 检查两个块之间是否存在关系
     * @param sourceBlockId 源块ID
     * @param targetBlockId 目标块ID
     * @param userId 用户ID
     * @return 是否存在关系
     */
    boolean relationExists(String sourceBlockId, String targetBlockId, String userId);
    
    /**
     * 获取两个块之间的关系
     * @param sourceBlockId 源块ID
     * @param targetBlockId 目标块ID
     * @param userId 用户ID
     * @return 关系对象，如果不存在则返回null
     */
    BlockRelation getRelationBetweenBlocks(String sourceBlockId, String targetBlockId, String userId);
} 