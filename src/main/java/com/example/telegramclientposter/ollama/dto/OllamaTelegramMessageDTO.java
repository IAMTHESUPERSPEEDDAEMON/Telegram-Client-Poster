package com.example.telegramclientposter.ollama.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OllamaTelegramMessageDTO {
    private long channelId;
    private String imageId;
    private String text;
    private String processedText;
}