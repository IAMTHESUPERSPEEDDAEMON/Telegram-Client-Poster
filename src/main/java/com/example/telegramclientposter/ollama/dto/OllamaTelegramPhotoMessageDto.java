package com.example.telegramclientposter.ollama.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OllamaTelegramPhotoMessageDto {
    private long chatId;
    private Long albumId;
    private List<Integer> fileIds;
    private String caption;
    private String processedText;
}