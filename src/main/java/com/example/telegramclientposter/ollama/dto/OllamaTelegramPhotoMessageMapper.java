package com.example.telegramclientposter.ollama.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OllamaTelegramPhotoMessageMapper {

    @Mapping(target = "processedText", ignore = true)
    OllamaTelegramPhotoMessageDto toOllamaTelegramPhotoMessageDTO(long chatId, List<Integer> fileIds, String text);
}
