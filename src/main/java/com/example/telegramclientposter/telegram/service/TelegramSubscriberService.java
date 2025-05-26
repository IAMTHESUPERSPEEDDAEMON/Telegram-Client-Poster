package com.example.telegramclientposter.telegram.service;

import com.example.telegramclientposter.repository.SubscriberRepository;
import it.tdlight.jni.TdApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
public class TelegramSubscriberService {

    private final SubscriberRepository subscriberRepository;

    @Autowired
    public TelegramSubscriberService(SubscriberRepository subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
    }

    public void processNewSubscriber(TdApi.Message message) {
        if (message.content instanceof TdApi.MessageChatJoinByLink joinEvent) {
            Subscriber subscriber = new Subscriber(
                    joinEvent.userId,
                    Instant.now().getEpochSecond(),
                    false
            );

            subscriberRepository.save(subscriber);
            log.info("New subscriber added: {}", joinEvent.userId);
        }
    }
}