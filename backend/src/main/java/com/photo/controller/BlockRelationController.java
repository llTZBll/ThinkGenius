package com.photo.controller;

import com.photo.entity.BlockRelation;
import com.photo.service.BlockRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/relations")
@CrossOrigin(origins = "*")
public class BlockRelationController {
    
    @Autowired
    private BlockRelationService blockRelationService;
    
    // 创建关系
    @PostMapping
    public ResponseEntity<BlockRelation> createRelation(@RequestBody Map<String, String> request,
                                                       @RequestParam String userId) {
        try {
            String sourceBlockId = request.get("sourceBlockId");
            String targetBlockId = request.get("targetBlockId");
            String relationType = request.get("relationType");
            
            BlockRelation relation = blockRelationService.createRelation(sourceBlockId, targetBlockId, relationType, userId);
            return ResponseEntity.ok(relation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 根据ID获取关系
    @GetMapping("/{id}")
    public ResponseEntity<BlockRelation> getRelationById(@PathVariable String id,
                                                        @RequestParam String userId) {
        try {
            BlockRelation relation = blockRelationService.getRelationById(id, userId);
            return ResponseEntity.ok(relation);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 获取用户的所有关系
    @GetMapping
    public ResponseEntity<List<BlockRelation>> getAllRelations(@RequestParam String userId) {
        try {
            List<BlockRelation> relations = blockRelationService.getAllRelationsByUserId(userId);
            return ResponseEntity.ok(relations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 获取与指定块相关的所有关系
    @GetMapping("/block/{blockId}")
    public ResponseEntity<List<BlockRelation>> getRelationsByBlockId(@PathVariable String blockId,
                                                                    @RequestParam String userId) {
        try {
            List<BlockRelation> relations = blockRelationService.getRelationsByBlockId(blockId, userId);
            return ResponseEntity.ok(relations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 根据关系类型获取关系
    @GetMapping("/type/{relationType}")
    public ResponseEntity<List<BlockRelation>> getRelationsByType(@PathVariable String relationType,
                                                                 @RequestParam String userId) {
        try {
            List<BlockRelation> relations = blockRelationService.getRelationsByType(userId, relationType);
            return ResponseEntity.ok(relations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 更新关系类型
    @PutMapping("/{id}")
    public ResponseEntity<BlockRelation> updateRelationType(@PathVariable String id,
                                                           @RequestBody Map<String, String> request,
                                                           @RequestParam String userId) {
        try {
            String relationType = request.get("relationType");
            BlockRelation relation = blockRelationService.updateRelationType(id, relationType, userId);
            return ResponseEntity.ok(relation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 删除关系
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRelation(@PathVariable String id,
                                              @RequestParam String userId) {
        try {
            blockRelationService.deleteRelation(id, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 删除与指定块相关的所有关系
    @DeleteMapping("/block/{blockId}")
    public ResponseEntity<Void> deleteRelationsByBlockId(@PathVariable String blockId,
                                                        @RequestParam String userId) {
        try {
            blockRelationService.deleteRelationsByBlockId(blockId, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 删除用户的所有关系
    @DeleteMapping
    public ResponseEntity<Void> deleteAllRelations(@RequestParam String userId) {
        try {
            blockRelationService.deleteAllRelationsByUserId(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 检查两个块之间是否存在关系
    @PostMapping("/exists")
    public ResponseEntity<Map<String, Boolean>> checkRelationExists(@RequestBody Map<String, String> request,
                                                                   @RequestParam String userId) {
        try {
            String sourceBlockId = request.get("sourceBlockId");
            String targetBlockId = request.get("targetBlockId");
            
            boolean exists = blockRelationService.relationExists(sourceBlockId, targetBlockId, userId);
            return ResponseEntity.ok(Map.of("exists", exists));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 获取两个块之间的关系
    @PostMapping("/between")
    public ResponseEntity<BlockRelation> getRelationBetweenBlocks(@RequestBody Map<String, String> request,
                                                                 @RequestParam String userId) {
        try {
            String sourceBlockId = request.get("sourceBlockId");
            String targetBlockId = request.get("targetBlockId");
            
            BlockRelation relation = blockRelationService.getRelationBetweenBlocks(sourceBlockId, targetBlockId, userId);
            if (relation != null) {
                return ResponseEntity.ok(relation);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 批量创建关系
    @PostMapping("/batch")
    public ResponseEntity<List<BlockRelation>> createRelationsBatch(@RequestBody List<Map<String, String>> requests,
                                                                   @RequestParam String userId) {
        try {
            List<BlockRelation> createdRelations = new java.util.ArrayList<>();
            
            for (Map<String, String> request : requests) {
                String sourceBlockId = request.get("sourceBlockId");
                String targetBlockId = request.get("targetBlockId");
                String relationType = request.get("relationType");
                
                BlockRelation relation = blockRelationService.createRelation(sourceBlockId, targetBlockId, relationType, userId);
                createdRelations.add(relation);
            }
            
            return ResponseEntity.ok(createdRelations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 获取关系统计信息
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getRelationStats(@RequestParam String userId) {
        try {
            List<BlockRelation> allRelations = blockRelationService.getAllRelationsByUserId(userId);
            
            long totalRelations = allRelations.size();
            long relatedCount = allRelations.stream().filter(r -> "related".equals(r.getRelationType())).count();
            long parentCount = allRelations.stream().filter(r -> "parent".equals(r.getRelationType())).count();
            long childCount = allRelations.stream().filter(r -> "child".equals(r.getRelationType())).count();
            long similarCount = allRelations.stream().filter(r -> "similar".equals(r.getRelationType())).count();
            
            Map<String, Object> stats = Map.of(
                "totalRelations", totalRelations,
                "relatedCount", relatedCount,
                "parentCount", parentCount,
                "childCount", childCount,
                "similarCount", similarCount
            );
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 