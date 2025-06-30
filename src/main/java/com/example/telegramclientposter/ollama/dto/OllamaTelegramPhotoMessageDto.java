package com.example.telegramclientposter.ollama.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OllamaTelegramPhotoMessageDto extends BaseTelegramMessageDto {
    private long albumId;
    private List<Integer> fileIds;
    private String caption;

    @Override
    public String getTextForOllama() {
        return this.caption;
    }
}