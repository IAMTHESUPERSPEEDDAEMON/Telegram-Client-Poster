package com.example.telegramclientposter.telegram.service;

import com.example.telegramclientposter.ollama.dto.OllamaTelegramMessageDTO;
import com.example.telegramclientposter.telegram.config.ChannelConfigLoader;
import it.tdlight.jni.TdApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Slf4j
@Service
public class ChannelMessageListener {
    private final ChannelConfigLoader channelConfig;

    @Autowired
    public ChannelMessageListener(ChannelConfigLoader channelConfig) {
        this.channelConfig = channelConfig;;
    }

    public boolean isMonitoredChannel(long chatId) {
        return channelConfig.isMonitoredChannel(chatId);
    }

    public OllamaTelegramMessageDTO processMessage(TdApi.UpdateNewMessage update) {
        TdApi.Message message = update.message;

        if (isMonitoredChannel(message.chatId)) {
            TdApi.MessageContent content = message.content;

        }
    }

}
