package com.example.telegramclientposter.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component // Маркируем как общий компонент Spring
@Slf4j
@Getter // Lombok для автоматической генерации геттеров
public class PromptProvider {

    private final ResourceLoader resourceLoader;

    // Путь к файлу промпта. Теперь по умолчанию указывает на 'classpath:prompts/channel-channel-post-prompt.yml'
    @Value("${app.prompts.channel-post-path:classpath:prompts/channel-post-prompt.yml}")
    private String channelPostPromptPath;

    private String systemMessage;
    private String formatInstructions;
    private String lengthConstraint;
    private String userInputPlaceholder;

    private static class PromptTemplateData {
        public String system_message;
        public String format_instructions;
        public String length_constraint;
        public String user_input_placeholder;
    }

    @Autowired
    public PromptProvider(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    // Метод, который будет вызван Spring'ом сразу после создания и инжектирования бина
    @PostConstruct
    public void loadPrompt() {
        log.info("Attempting to load channel post prompt from: {}", channelPostPromptPath);

        ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

        try {
            Resource resource = resourceLoader.getResource(channelPostPromptPath);
            if (!resource.exists()) {
                throw new IOException("Prompt file not found at: " + channelPostPromptPath);
            }
            try (InputStream inputStream = resource.getInputStream()) {
                String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

                // Десериализуем YAML-строку в наш вспомогательный объект PromptTemplateData
                PromptTemplateData data = yamlMapper.readValue(content, PromptTemplateData.class);

                // Сохраняем загруженные части промпта в полях класса, удаляя лишние пробелы
                this.systemMessage = data.system_message.trim();
                this.formatInstructions = data.format_instructions.trim();
                this.lengthConstraint = data.length_constraint.trim();
                this.userInputPlaceholder = data.user_input_placeholder.trim();

                log.info("Successfully loaded channel post prompt from {}.", channelPostPromptPath);
                // Для отладки можно выводить содержимое:
                // log.debug("System Message loaded: \n{}", systemMessage);
                // log.debug("Format Instructions loaded: \n{}", formatInstructions);
                // log.debug("Length Constraint loaded: \n{}", lengthConstraint);
            }
        } catch (IOException e) {
            log.error("Failed to load channel post prompt from {}. Error: {}", channelPostPromptPath, e.getMessage(), e);
            // Бросаем RuntimeException, чтобы Spring остановил запуск приложения.
            throw new RuntimeException("Failed to initialize PromptProvider: Could not load prompt.", e);
        }
    }

    /**
     * Собирает полный текст промпта для отправки в LLM, подставляя оригинальный текст.
     *
     * @param originalText Исходный текст (например, подпись к фото) для вставки в промпт.
     * @return Полный текст промпта, готовый для LLM.
     */
    public String getFullPrompt(String originalText) {
        // Переносы строк ('\n\n') добавляются для лучшей читаемости промпта LLM.
        String fullPrompt = String.format("%s\n\n%s\n\n%s\n\n%s",
                systemMessage,
                formatInstructions,
                lengthConstraint,
                userInputPlaceholder.replace("{original_text}", originalText != null ? originalText : ""));
        return fullPrompt;
    }
}
