package com.photo.service;

import com.photo.entity.Block;
import com.photo.entity.BlockRelation;

import java.util.List;

public interface BlockPositionService {
    
    /**
     * 为新块计算最佳位置
     * @param newBlock 新创建的块
     * @param existingBlocks 现有的所有块
     * @param relations 块之间的关系
     * @return 计算出的最佳位置
     */
    Block.Position calculateOptimalPosition(Block newBlock, List<Block> existingBlocks, List<BlockRelation> relations);
    
    /**
     * 重新布局所有块的位置
     * @param blocks 所有块
     * @param relations 块之间的关系
     * @return 重新布局后的块列表
     */
    List<Block> relayoutAllBlocks(List<Block> blocks, List<BlockRelation> relations);
    
    /**
     * 检查位置是否可用（不与其他块重叠）
     * @param position 要检查的位置
     * @param size 块的大小
     * @param existingBlocks 现有的块
     * @param excludeBlockId 要排除的块ID（用于更新时）
     * @return 是否可用
     */
    boolean isPositionAvailable(Block.Position position, Block.Size size, List<Block> existingBlocks, String excludeBlockId);
    
    /**
     * 找到最近的可用位置
     * @param preferredPosition 首选位置
     * @param size 块的大小
     * @param existingBlocks 现有的块
     * @param excludeBlockId 要排除的块ID
     * @return 最近的可用位置
     */
    Block.Position findNearestAvailablePosition(Block.Position preferredPosition, Block.Size size, List<Block> existingBlocks, String excludeBlockId);
    
    /**
     * 根据关系类型计算相关块的位置
     * @param sourceBlock 源块
     * @param targetBlock 目标块
     * @param relationType 关系类型
     * @return 建议的目标块位置
     */
    Block.Position calculateRelatedBlockPosition(Block sourceBlock, Block targetBlock, String relationType);
} 