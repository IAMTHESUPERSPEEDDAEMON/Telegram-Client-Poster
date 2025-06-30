package com.example.telegramclientposter.repository;

import com.example.telegramclientposter.entity.MessageEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageEmbeddingRepository extends JpaRepository<MessageEmbedding, Long> {
    List<MessageEmbedding> findTop10ByOrderByCreatedAtDesc();
}
