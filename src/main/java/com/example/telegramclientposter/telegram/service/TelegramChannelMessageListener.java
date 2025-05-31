package com.example.telegramclientposter.telegram.service;

import com.example.telegramclientposter.ollama.dto.OllamaTelegramMessageMapper;
import com.example.telegramclientposter.ollama.dto.OllamaTelegramMessageDTO;
import com.example.telegramclientposter.telegram.config.ChannelConfigLoader;
import it.tdlight.jni.TdApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import static com.example.telegramclientposter.constanta.Constants.*;

import java.util.Arrays;
import java.util.Comparator;


@Slf4j
@Service
public class TelegramChannelMessageListener {
    private final ChannelConfigLoader channelConfig;
    private final OllamaTelegramMessageMapper ollamaTelegramMessageMapper;
    private final RabbitTemplate rabbitTemplate;

    @Value("${tg.my-channel-id}")
    private long targetChatId;

    @Autowired
    public TelegramChannelMessageListener(ChannelConfigLoader channelConfig, OllamaTelegramMessageMapper ollamaTelegramMessageMapper, RabbitTemplate rabbitTemplate) {
        this.channelConfig = channelConfig;
        this.ollamaTelegramMessageMapper = ollamaTelegramMessageMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    public boolean isMonitoredChannel(long chatId) {
        return channelConfig.isMonitoredChannel(chatId);
    }

    public void processMessage(TdApi.UpdateNewMessage update) {
        TdApi.Message message = update.message;
        String photoCaption = "";
        int fileId = 0;

        if (isMonitoredChannel(message.chatId) & message.content instanceof TdApi.MessagePhoto) {
            TdApi.MessagePhoto messagePhoto = (TdApi.MessagePhoto) message.content;

            if (messagePhoto.caption != null && messagePhoto.caption.text != null) {
                photoCaption = messagePhoto.caption.text;
                log.info("Photo caption RECEIVED");
            }

            if (messagePhoto.photo != null ) {
                TdApi.PhotoSize largestPhotoSize = Arrays.stream(messagePhoto.photo.sizes)
                        .max(Comparator.comparingInt(photoSize -> photoSize.width * photoSize.height))
                        .orElse(null);

                if (largestPhotoSize != null && largestPhotoSize.photo != null) {
                    fileId = largestPhotoSize.photo.id;
                }
            }
            OllamaTelegramMessageDTO dto = ollamaTelegramMessageMapper.toOllamaTelegramMessageDTO(targetChatId, fileId, photoCaption);
            rabbitTemplate.convertAndSend(OLLAMA_EXCHANGE_NAME, OLLAMA_QUEUE_NAME, dto);
            log.info("Sent DTO to Ollama processing queue");
        } else {
            log.warn("Message from unmonitored channel or not a photo message. Chat ID: {} Content Type: {}",
                    message.chatId, message.content.getClass().getSimpleName());
        }
    }

}
