package com.example.telegramclientposter.ollama.mapper;

import com.example.telegramclientposter.ollama.dto.OllamaTelegramVideoMessageDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OllamaTelegramVideoMessageMapper {

    @Mapping(target = "processedText", ignore = true)
    OllamaTelegramVideoMessageDto toOllamaTelegramVideoMessageDTO(long chatId, List<Integer> fileIds, String caption);
}
