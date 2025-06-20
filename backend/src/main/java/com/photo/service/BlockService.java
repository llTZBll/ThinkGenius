package com.photo.service;

import com.photo.dto.BlockRequest;
import com.photo.entity.Block;

import java.util.List;

public interface BlockService {
    
    // 创建新块
    Block createBlock(BlockRequest request, String userId);
    
    // 根据ID获取块
    Block getBlockById(String id, String userId);
    
    // 获取用户的所有块
    List<Block> getAllBlocksByUserId(String userId);
    
    // 根据类型获取用户的块
    List<Block> getBlocksByType(String userId, String type);
    
    // 更新块
    Block updateBlock(String id, BlockRequest request, String userId);
    
    // 更新块位置
    Block updateBlockPosition(String id, Block.Position position, String userId);
    
    // 删除块
    void deleteBlock(String id, String userId);
    
    // 删除用户的所有块
    void deleteAllBlocksByUserId(String userId);
    
    // 位置算法相关方法
    
    /**
     * 创建块并自动计算位置
     * @param request 块创建请求
     * @param userId 用户ID
     * @return 创建的块
     */
    Block createBlockWithAutoPosition(BlockRequest request, String userId);
    
    /**
     * 重新布局用户的所有块
     * @param userId 用户ID
     * @return 重新布局后的块列表
     */
    List<Block> relayoutUserBlocks(String userId);
    
    /**
     * 检查块位置是否可用
     * @param position 位置
     * @param size 大小
     * @param userId 用户ID
     * @param excludeBlockId 排除的块ID
     * @return 是否可用
     */
    boolean isPositionAvailable(Block.Position position, Block.Size size, String userId, String excludeBlockId);
    
    /**
     * 找到最近的可用位置
     * @param preferredPosition 首选位置
     * @param size 块大小
     * @param userId 用户ID
     * @param excludeBlockId 排除的块ID
     * @return 最近的可用位置
     */
    Block.Position findNearestAvailablePosition(Block.Position preferredPosition, Block.Size size, String userId, String excludeBlockId);
} 