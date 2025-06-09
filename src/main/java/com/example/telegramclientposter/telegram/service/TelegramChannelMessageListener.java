package com.example.telegramclientposter.telegram.service;

import com.example.telegramclientposter.ollama.dto.BaseTelegramMessageDto;
import com.example.telegramclientposter.ollama.mapper.OllamaTelegramPhotoMessageMapper;
import com.example.telegramclientposter.ollama.dto.OllamaTelegramPhotoMessageDto;
import com.example.telegramclientposter.ollama.dto.OllamaTelegramTextMessageDto;
import com.example.telegramclientposter.ollama.mapper.OllamaTelegramTextMessageMapper;
import com.example.telegramclientposter.telegram.config.ChannelConfigLoader;
import it.tdlight.jni.TdApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import static com.example.telegramclientposter.constanta.Constants.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


@Slf4j
@Service
public class TelegramChannelMessageListener {
    private final ChannelConfigLoader channelConfig;
    private final OllamaTelegramPhotoMessageMapper ollamaTelegramPhotoMessageMapper;
    private final RabbitTemplate rabbitTemplate;
    private final OllamaTelegramTextMessageMapper ollamaTelegramTextMessageMapper;

    @Value("${tg.my-channel-id}")
    private long targetChatId;

    @Autowired
    public TelegramChannelMessageListener(ChannelConfigLoader channelConfig,
                                          OllamaTelegramPhotoMessageMapper ollamaTelegramPhotoMessageMapper,
                                          OllamaTelegramTextMessageMapper ollamaTelegramTextMessageMapper,
                                          RabbitTemplate rabbitTemplate) {
        this.channelConfig = channelConfig;
        this.ollamaTelegramPhotoMessageMapper = ollamaTelegramPhotoMessageMapper;
        this.ollamaTelegramTextMessageMapper = ollamaTelegramTextMessageMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    public boolean isMonitoredChannel(long chatId) {
        return channelConfig.isMonitoredChannel(chatId);
    }

    public void processMessage(TdApi.UpdateNewMessage update) {
        TdApi.Message message = update.message;

        if (isMonitoredChannel(message.chatId)) {
            BaseTelegramMessageDto dtoToSend = null;
            // Check message to be a photo message
            if (message.content instanceof TdApi.MessagePhoto messagePhoto) {
                log.info("PhotoMessage received");
                dtoToSend = processPhotoMessage(messagePhoto);
            } else if (message.content instanceof TdApi.MessageText messageText) {
                log.info("TextMessage Received");
                dtoToSend = processTextMessage(messageText);
            }

            if (dtoToSend != null) {
                rabbitTemplate.convertAndSend(OLLAMA_EXCHANGE_NAME, OLLAMA_QUEUE_NAME, dtoToSend);
                log.info("Sent DTO to Ollama processing queue");
            }
        } else {
            log.warn("Message from unmonitored channel. Chat ID: {} Content Type: {}",
                    message.chatId, message.content.getClass().getSimpleName());
        }
    }

    private OllamaTelegramPhotoMessageDto processPhotoMessage(TdApi.MessagePhoto messagePhoto) {

        List<Integer> fileIds = new ArrayList<>();
        long albumId = 0L;

        if (!messagePhoto.caption.text.trim().isEmpty() && messagePhoto.photo != null) {
            String photoCaption = messagePhoto.caption.text;
            log.info("Photo caption RECEIVED");
            TdApi.PhotoSize largestPhotoSize = Arrays.stream(messagePhoto.photo.sizes)
                    .max(Comparator.comparingInt(photoSize -> photoSize.width * photoSize.height))
                    .orElse(null);

            if (largestPhotoSize != null && largestPhotoSize.photo != null) {
                fileIds.add(largestPhotoSize.photo.id);
            }
            return ollamaTelegramPhotoMessageMapper.toOllamaTelegramPhotoMessageDTO(targetChatId, albumId, fileIds, photoCaption);
        } else {
            return null;
        }
    }

    private OllamaTelegramTextMessageDto processTextMessage(TdApi.MessageText messageText) {

        if (messageText.text != null && messageText.text.text != null) {
            String originalText = messageText.text.text;
            return ollamaTelegramTextMessageMapper.toOllamaTelegramMessageDto(targetChatId, originalText);
        } else
            return null;
    }
}
