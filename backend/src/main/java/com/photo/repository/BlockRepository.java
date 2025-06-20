package com.photo.repository;

import com.photo.entity.Block;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlockRepository extends MongoRepository<Block, String> {
    
    // 根据用户ID查找所有块
    List<Block> findByUserId(String userId);
    
    // 根据用户ID和类型查找块
    List<Block> findByUserIdAndType(String userId, String type);
    
    // 根据用户ID删除所有块
    void deleteByUserId(String userId);
} 