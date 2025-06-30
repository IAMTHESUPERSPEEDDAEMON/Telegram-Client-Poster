package com.example.telegramclientposter.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "message_embeddings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageEmbedding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_text", columnDefinition = "TEXT", nullable = false)
    private String messageText;

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(name = "embedding", columnDefinition = "vector(4096)", nullable = false)
    private float[] embedding;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}