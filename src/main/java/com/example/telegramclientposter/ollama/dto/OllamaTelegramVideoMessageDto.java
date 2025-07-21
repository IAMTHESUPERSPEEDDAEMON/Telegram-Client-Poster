package com.example.telegramclientposter.ollama.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OllamaTelegramVideoMessageDto extends BaseTelegramMessageDto {
    private List<Integer> fileIds;
    private String caption;

    @Override
    public String getTextForOllama() {
        return this.caption;
    }
}
