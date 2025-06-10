package com.example.telegramclientposter.telegram.service;

import it.tdlight.client.SimpleTelegramClient;
import it.tdlight.jni.TdApi;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TelegramClientService {
    private final TelegramChannelMessageListener messageListener;
    private final SimpleTelegramClient telegramClient;

    @Autowired
    public TelegramClientService(TelegramChannelMessageListener messageListener,
                                 SimpleTelegramClient telegramClient) {
        this.messageListener = messageListener;
        this.telegramClient = telegramClient;

    }

    @PostConstruct
    public void registerUpdateHandlers() {
        // Register the onUpdate method as a handler for new messages
        telegramClient.addUpdateHandler(TdApi.UpdateNewMessage.class, this::onUpdate);
        log.info("TelegramClientService registered as UpdateNewMessage handler.");
    }

    public void onUpdate(TdApi.UpdateNewMessage update) {
        messageListener.processMessage(update);
    }

}


