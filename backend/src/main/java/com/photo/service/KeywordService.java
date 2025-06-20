package com.photo.service;

import com.photo.entity.Keyword;
import com.photo.repository.KeywordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KeywordService {

    @Autowired
    private KeywordRepository keywordRepository;

    public List<Keyword> findByUserId(String userId) {
        return keywordRepository.findByUserId(userId);
    }

    public Keyword save(Keyword keyword) {
        return keywordRepository.save(keyword);
    }

    public void delete(String id) {
        keywordRepository.deleteById(id);
    }

    public Optional<Keyword> findById(String id) {
        return keywordRepository.findById(id);
    }
} 