package com.example.telegramclientposter.ollama.service;

import com.example.telegramclientposter.ollama.dto.BaseTelegramMessageDto;
import com.example.telegramclientposter.util.PromptProviderMain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
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
public class OllamaMainService {

    private final RabbitTemplate rabbitTemplate;
    private final OllamaChatModel chatModel;
    private final PromptProviderMain promptProvider;

    @Autowired
    public OllamaMainService(RabbitTemplate rabbitTemplate, OllamaChatModel chatModel, PromptProviderMain promptProvider) {
        this.rabbitTemplate = rabbitTemplate;
        this.chatModel = chatModel;
        this.promptProvider = promptProvider;
    }

    @RabbitListener(queues = OLLAMA_VALID_QUEUE_NAME)
    public void processMessageWithOllama(BaseTelegramMessageDto dto) {
        log.info("OllamaService received message from queue");

        boolean shouldSend = false;

        try {
            String originalText = dto.getTextForOllama();
            String processedTextFromOllama = callToOllama(originalText);

            dto.setProcessedText(processedTextFromOllama);
            log.info("OllamaService done processing caption from Ollama");
            shouldSend = true;
        } catch (RuntimeException e) {
            log.error("Ollama processing interrupted for DTO: {}", dto, e);
        }

        if (shouldSend) {
            rabbitTemplate.convertAndSend(TELEGRAM_SEND_EXCHANGE_NAME, TELEGRAM_SEND_QUEUE_NAME, dto);
            log.info("Sent processed DTO of type {} to Telegram sending queue.", dto.getClass().getSimpleName());
        } else {
            log.warn("Skipped sending DTO to Telegram queue due to error or unsupported type for DTO: {}", dto);
        }
    }

    private String callToOllama(String text) {

        Prompt prompt = new Prompt(promptProvider.getFullPrompt(text));
        ChatResponse response = chatModel.call(prompt);

        if (response != null && response.getResult() != null && response.getResult().getOutput() != null) {
            AssistantMessage assistantMessage = response.getResult().getOutput();
            log.info("OllamaService callToOllama() returned: {}", assistantMessage.getText());
            return assistantMessage.getText();
        } else {
            log.warn("Received empty or invalid response from Ollama for prompt: {}", text);
            return "Не удалось обработать запрос.";
        }
    }
}
