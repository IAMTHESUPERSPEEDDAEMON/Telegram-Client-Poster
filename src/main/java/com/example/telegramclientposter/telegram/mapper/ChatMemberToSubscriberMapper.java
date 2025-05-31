package com.example.telegramclientposter.telegram.mapper;

import com.example.telegramclientposter.entity.Subscriber;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Date;

@Mapper(componentModel = "spring")
public interface ChatMemberToSubscriberMapper {

    @Mapping(target = "id", ignore = true)
    Subscriber subscriber(long telegramId, String username, long source, Date created);
}
