package com.example.telegramclientposter.ollama.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OllamaTelegramMessageMapper {

    OllamaTelegramMessageMapper INSTANCE = Mappers.getMapper(OllamaTelegramMessageMapper.class);

    @Mapping(target = "processedText", ignore = true)
    OllamaTelegramMessageDTO toOllamaTelegramMessageDTO(long chatId, int fileId, String text);
}
