package com.example.telegramclientposter.ollama.mapper;

import com.example.telegramclientposter.ollama.dto.OllamaTelegramTextMessageDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OllamaTelegramTextMessageMapper {

    @Mapping(target = "processedText", ignore = true)
    OllamaTelegramTextMessageDto toOllamaTelegramMessageDto(long chatId, String originalText);
}
