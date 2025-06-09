package com.example.telegramclientposter.ollama.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OllamaTelegramTextMessageDto {
    private long chatId;
    private String originalText;
    private String processedText;
}
