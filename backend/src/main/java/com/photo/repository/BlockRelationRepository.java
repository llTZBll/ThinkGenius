package com.photo.repository;

import com.photo.entity.BlockRelation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlockRelationRepository extends MongoRepository<BlockRelation, String> {
    
    // 根据用户ID查找所有关系
    List<BlockRelation> findByUserId(String userId);
    
    // 根据源块ID查找关系
    List<BlockRelation> findBySourceBlockId(String sourceBlockId);
    
    // 根据目标块ID查找关系
    List<BlockRelation> findByTargetBlockId(String targetBlockId);
    
    // 根据用户ID和源块ID查找关系
    List<BlockRelation> findByUserIdAndSourceBlockId(String userId, String sourceBlockId);
    
    // 根据用户ID和目标块ID查找关系
    List<BlockRelation> findByUserIdAndTargetBlockId(String userId, String targetBlockId);
    
    // 根据关系类型查找
    List<BlockRelation> findByUserIdAndRelationType(String userId, String relationType);
    
    // 删除与特定块相关的所有关系
    void deleteBySourceBlockIdOrTargetBlockId(String sourceBlockId, String targetBlockId);
} 