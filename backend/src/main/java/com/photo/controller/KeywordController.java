package com.photo.controller;

import com.photo.entity.Keyword;
import com.photo.service.KeywordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/keywords")
@CrossOrigin
public class KeywordController {

    @Autowired
    private KeywordService keywordService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Keyword>> getByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(keywordService.findByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<Keyword> save(@RequestBody Keyword keyword) {
        return ResponseEntity.ok(keywordService.save(keyword));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        keywordService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Keyword> getById(@PathVariable String id) {
        return keywordService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
} 