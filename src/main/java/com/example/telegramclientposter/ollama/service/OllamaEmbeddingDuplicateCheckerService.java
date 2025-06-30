package com.example.telegramclientposter.ollama.service;

import com.example.telegramclientposter.entity.MessageEmbedding;
import com.example.telegramclientposter.ollama.dto.BaseTelegramMessageDto;
import com.example.telegramclientposter.repository.MessageEmbeddingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.telegramclientposter.constanta.Constants.*;

@Slf4j
@Service
public class OllamaEmbeddingDuplicateCheckerService {

    private final MessageEmbeddingRepository messageEmbeddingRepository;
    private final RabbitTemplate rabbitTemplate;
    private final EmbeddingModel embeddingModel;

    public OllamaEmbeddingDuplicateCheckerService(RabbitTemplate rabbitTemplate,  EmbeddingModel embeddingModel,
                                                  MessageEmbeddingRepository messageEmbeddingRepository) {
        this.messageEmbeddingRepository = messageEmbeddingRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.embeddingModel = embeddingModel;
    }

    @RabbitListener(queues = OLLAMA_EMBEDDING_QUEUE_NAME)
    public void checkMessageForDuplicates(BaseTelegramMessageDto dto) {
        double maxSimilarity = 0.0;
        MessageEmbedding mostSimilarMessage = null;

        log.info("DuplicateChecker received message from queue");

        String cleanedText = dto.getCleanedTextForEmbedding();

        if (cleanedText == null || cleanedText.trim().isEmpty()) {
            log.warn("Cleaned text is empty or null");
            return;
        }

        log.debug("Generating embedding for received message from queue");
        float[] currentMessageEmbedding = embeddingModel.embed(cleanedText);

        log.debug("Get last 10 embeddings from embedding db");
        List<MessageEmbedding> lastEmbeddings = messageEmbeddingRepository.findTop10ByOrderByCreatedAtDesc();

        for (MessageEmbedding existing : lastEmbeddings) {
            double similarity = calculateCosineSimilarity(currentMessageEmbedding, existing.getEmbedding());

            if (similarity > maxSimilarity) {
                maxSimilarity = similarity;
                mostSimilarMessage = existing;
            }
        }

        // check for similarity
        if (maxSimilarity >= SIMILARITY_THRESHOLD) {
            log.info("Duplicate message detected! Similarity: {} with message ID: {}",
                    maxSimilarity, mostSimilarMessage.getId());

            return;
        }

        MessageEmbedding newEmbedding = new MessageEmbedding();
        newEmbedding.setMessageText(cleanedText);
        newEmbedding.setEmbedding(currentMessageEmbedding);
        newEmbedding.setCreatedAt(LocalDateTime.now());

        messageEmbeddingRepository.save(newEmbedding);

        log.info("New unique message saved. Max similarity with existing messages: {}", maxSimilarity);

        rabbitTemplate.convertAndSend(OLLAMA_VALID_EXCHANGE_NAME, OLLAMA_VALID_QUEUE_NAME, dto);
        log.info("Sent DTO to Ollama-Embedding processing queue");
    }

    private double calculateCosineSimilarity(float[] vectorA, float[] vectorB) {
        if (vectorA.length != vectorB.length) {
            throw new IllegalArgumentException("Vectors must have the same length");
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += vectorA[i] * vectorA[i];
            normB += vectorB[i] * vectorB[i];
        }

        normA = Math.sqrt(normA);
        normB = Math.sqrt(normB);

        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }

        return dotProduct / (normA * normB);
    }
}
