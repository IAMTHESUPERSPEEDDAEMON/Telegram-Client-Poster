package com.example.telegramclientposter.ollama.service;

import com.example.telegramclientposter.ollama.dto.OllamaTelegramMessageDTO;
import com.example.telegramclientposter.util.PromptProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.telegramclientposter.constanta.Constants.*;

@Slf4j
@Service
public class OllamaService {

    private final RabbitTemplate rabbitTemplate;
    private final OllamaChatModel chatModel;
    private final PromptProvider promptProvider;

    @Autowired
    public OllamaService(RabbitTemplate rabbitTemplate, OllamaChatModel chatModel, PromptProvider promptProvider) {
        this.rabbitTemplate = rabbitTemplate;
        this.chatModel = chatModel;
        this.promptProvider = promptProvider;
    }

    @RabbitListener(queues = OLLAMA_QUEUE_NAME)
    public void processMessageWithOllama(OllamaTelegramMessageDTO dto) {
        log.info("OllamaService received message from queue: {}", dto);

        try {
            String originalText = dto.getText();
            String processedTextFromOllama = callToOllama(originalText);

            dto.setProcessedText(processedTextFromOllama);
            log.info("OllamaService processed message. New DTO: {}", dto);

            rabbitTemplate.convertAndSend(TELEGRAM_SEND_EXCHANGE_NAME, TELEGRAM_SEND_QUEUE_NAME, dto);
            log.info("Sent processed DTO to Telegram sending queue: {}", dto.getChatId());
        } catch (RuntimeException e) {
            log.error("Ollama processing interrupted for DTO: {}", dto, e);
        }
    }

    private String callToOllama(String text) {

        Prompt prompt = new Prompt(promptProvider.getFullPrompt(text));

        ChatResponse response = chatModel.call(prompt);

        if (response != null && response.getResult() != null && response.getResult().getOutput() != null) {
            log.info("OllamaService callToOllama() returned: {}", response);
            return response.getResult().getOutput().toString();
        } else {
            log.warn("Received empty or invalid response from Ollama for prompt: {}", text);
            return "Не удалось обработать запрос.";
        }
    }
}
