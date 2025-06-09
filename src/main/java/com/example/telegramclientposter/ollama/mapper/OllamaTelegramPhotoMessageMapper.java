package com.example.telegramclientposter.ollama.mapper;

import com.example.telegramclientposter.ollama.dto.OllamaTelegramPhotoMessageDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OllamaTelegramPhotoMessageMapper {

    @Mapping(target = "processedText", ignore = true)
    OllamaTelegramPhotoMessageDto toOllamaTelegramPhotoMessageDTO(long chatId, long albumId, List<Integer> fileIds, String caption);
}
