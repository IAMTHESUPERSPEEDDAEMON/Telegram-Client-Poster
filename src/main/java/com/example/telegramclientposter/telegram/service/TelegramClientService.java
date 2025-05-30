package com.example.telegramclientposter.telegram.service;

import it.tdlight.jni.TdApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TelegramClientService {
    private final TelegramChannelMessageListener messageListener;
    private final TelegramSubscriberService subscriberService;

    @Autowired
    public TelegramClientService(TelegramChannelMessageListener messageListener,
                                 TelegramSubscriberService subscriberService) {
        this.messageListener = messageListener;
        this.subscriberService = subscriberService;

    }

    public void onUpdate(TdApi.UpdateNewMessage update) {
        TdApi.Message message = update.message;

        // Определяем тип чата и делегируем обработку
        if (message.isChannelPost) {
            messageListener.processMessage(update);
        }
    }

}


