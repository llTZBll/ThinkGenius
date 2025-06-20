package com.photo.controller;

import com.photo.dto.BlockRequest;
import com.photo.entity.Block;
import com.photo.service.BlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/blocks")
@CrossOrigin(origins = "*")
public class BlockController {
    
    @Autowired
    private BlockService blockService;
    
    // 创建新块
    @PostMapping
    public ResponseEntity<Block> createBlock(@RequestBody BlockRequest request, 
                                           @RequestParam String userId) {
        try {
            Block block = blockService.createBlock(request, userId);
            return ResponseEntity.ok(block);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 创建新块并自动计算位置
    @PostMapping("/auto-position")
    public ResponseEntity<Block> createBlockWithAutoPosition(@RequestBody BlockRequest request, 
                                                           @RequestParam String userId) {
        try {
            Block block = blockService.createBlockWithAutoPosition(request, userId);
            return ResponseEntity.ok(block);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 获取所有块
    @GetMapping
    public ResponseEntity<List<Block>> getAllBlocks(@RequestParam String userId) {
        try {
            List<Block> blocks = blockService.getAllBlocksByUserId(userId);
            return ResponseEntity.ok(blocks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 根据类型获取块
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Block>> getBlocksByType(@RequestParam String userId, 
                                                      @PathVariable String type) {
        try {
            List<Block> blocks = blockService.getBlocksByType(userId, type);
            return ResponseEntity.ok(blocks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 根据ID获取块
    @GetMapping("/{id}")
    public ResponseEntity<Block> getBlockById(@PathVariable String id, 
                                             @RequestParam String userId) {
        try {
            Block block = blockService.getBlockById(id, userId);
            return ResponseEntity.ok(block);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 更新块
    @PutMapping("/{id}")
    public ResponseEntity<Block> updateBlock(@PathVariable String id, 
                                           @RequestBody BlockRequest request, 
                                           @RequestParam String userId) {
        try {
            Block block = blockService.updateBlock(id, request, userId);
            return ResponseEntity.ok(block);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 更新块位置
    @PatchMapping("/{id}/position")
    public ResponseEntity<Block> updateBlockPosition(@PathVariable String id, 
                                                   @RequestBody Block.Position position, 
                                                   @RequestParam String userId) {
        try {
            Block block = blockService.updateBlockPosition(id, position, userId);
            return ResponseEntity.ok(block);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 删除块
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlock(@PathVariable String id, 
                                          @RequestParam String userId) {
        try {
            blockService.deleteBlock(id, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 删除用户的所有块
    @DeleteMapping
    public ResponseEntity<Void> deleteAllBlocks(@RequestParam String userId) {
        try {
            blockService.deleteAllBlocksByUserId(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 位置算法相关API
    
    /**
     * 重新布局用户的所有块
     */
    @PostMapping("/relayout")
    public ResponseEntity<List<Block>> relayoutAllBlocks(@RequestParam String userId) {
        try {
            List<Block> relayoutedBlocks = blockService.relayoutUserBlocks(userId);
            return ResponseEntity.ok(relayoutedBlocks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 检查位置是否可用
     */
    @PostMapping("/check-position")
    public ResponseEntity<Map<String, Boolean>> checkPosition(@RequestBody Map<String, Object> request,
                                                             @RequestParam String userId) {
        try {
            Block.Position position = new Block.Position(
                ((Number) request.get("x")).doubleValue(),
                ((Number) request.get("y")).doubleValue()
            );
            
            Block.Size size = new Block.Size(
                ((Number) request.get("width")).doubleValue(),
                ((Number) request.get("height")).doubleValue()
            );
            
            String excludeBlockId = (String) request.get("excludeBlockId");
            
            boolean isAvailable = blockService.isPositionAvailable(position, size, userId, excludeBlockId);
            
            return ResponseEntity.ok(Map.of("available", isAvailable));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 找到最近的可用位置
     */
    @PostMapping("/find-nearest-position")
    public ResponseEntity<Block.Position> findNearestPosition(@RequestBody Map<String, Object> request,
                                                             @RequestParam String userId) {
        try {
            Block.Position preferredPosition = new Block.Position(
                ((Number) request.get("x")).doubleValue(),
                ((Number) request.get("y")).doubleValue()
            );
            
            Block.Size size = new Block.Size(
                ((Number) request.get("width")).doubleValue(),
                ((Number) request.get("height")).doubleValue()
            );
            
            String excludeBlockId = (String) request.get("excludeBlockId");
            
            Block.Position nearestPosition = blockService.findNearestAvailablePosition(
                preferredPosition, size, userId, excludeBlockId);
            
            return ResponseEntity.ok(nearestPosition);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 获取画布配置信息
     */
    @GetMapping("/canvas-config")
    public ResponseEntity<Map<String, Object>> getCanvasConfig() {
        Map<String, Object> config = Map.of(
            "width", 2000.0,
            "height", 1500.0,
            "minBlockDistance", 50.0,
            "gridSize", 20.0,
            "defaultSizes", Map.of(
                "question", Map.of("width", 300.0, "height", 150.0),
                "keyword", Map.of("width", 200.0, "height", 100.0),
                "text", Map.of("width", 400.0, "height", 200.0)
            )
        );
        
        return ResponseEntity.ok(config);
    }
} 