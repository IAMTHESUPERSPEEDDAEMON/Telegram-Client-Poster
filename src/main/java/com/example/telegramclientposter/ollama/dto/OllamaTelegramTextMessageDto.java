package com.example.telegramclientposter.ollama.dto;

import lombok.*;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OllamaTelegramTextMessageDto extends BaseTelegramMessageDto {
    private String originalText;

    @Override
    public String getTextForOllama() {
        return this.originalText;
    }
}
