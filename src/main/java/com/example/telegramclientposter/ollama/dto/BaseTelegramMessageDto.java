package com.example.telegramclientposter.ollama.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@com.fasterxml.jackson.annotation.JsonTypeInfo(
        use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME,
        include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY,
        property = "type" // Имя поля в JSON, которое будет хранить тип (например, "PhotoMessage")
)
// @JsonSubTypes указывает, какие подтипы существуют для этого базового класса
@com.fasterxml.jackson.annotation.JsonSubTypes({
        @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = OllamaTelegramPhotoMessageDto.class, name = "PhotoMessage"),
        @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = OllamaTelegramTextMessageDto.class, name = "TextMessage")
})
public abstract class BaseTelegramMessageDto {
    protected long chatId;
    protected String processedText;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected String cleanedTextForEmbedding;

    public abstract String getTextForOllama();
}
