package com.photo.repository;

import com.photo.entity.Keyword;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KeywordRepository extends MongoRepository<Keyword, String> {
    List<Keyword> findByUserId(String userId);
} 