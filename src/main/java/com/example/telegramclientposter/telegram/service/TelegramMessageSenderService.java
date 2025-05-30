package com.example.telegramclientposter.telegram.service;

import com.example.telegramclientposter.ollama.dto.OllamaTelegramMessageDTO;
import it.tdlight.client.SimpleTelegramClient;
import it.tdlight.jni.TdApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.telegramclientposter.constanta.Constants.*;

@Slf4j
@Service
public class TelegramMessageSenderService {

    private final SimpleTelegramClient telegramClient;

    @Autowired
    public TelegramMessageSenderService(SimpleTelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    @RabbitListener(queues = TELEGRAM_SEND_QUEUE_NAME)
    public void sendMessageToTelegram(OllamaTelegramMessageDTO dto) {
        log.info("Sending message: {}", dto);

        try {
            long chatId = dto.getChatId();
            int fileId = dto.getFileId();
            String processedText = dto.getProcessedText();

            if (processedText == null || processedText.trim().isEmpty()) {
                log.warn("Processed text is empty for DTO: {}. Skipping message sending.", dto);
                return;
            }

            TdApi.FormattedText formattedText = new TdApi.FormattedText(processedText, new TdApi.TextEntity[0]);
            TdApi.InputMessagePhoto messagePhoto = new TdApi.InputMessagePhoto();

            messagePhoto.photo = new TdApi.InputFileId(fileId);
            messagePhoto.caption = formattedText;

            TdApi.SendMessage sendMessage = new TdApi.SendMessage();
            sendMessage.chatId = chatId;
            sendMessage.inputMessageContent = messagePhoto;

            telegramClient.send(sendMessage, result -> {
                if (result.isError()) {
                    log.error("Error sending message to chat ID {}: {}", chatId, result.getError().message);
                } else {
                    log.info("Message sent to chat ID {}", chatId);
                }
            });
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
        }
    }

}
