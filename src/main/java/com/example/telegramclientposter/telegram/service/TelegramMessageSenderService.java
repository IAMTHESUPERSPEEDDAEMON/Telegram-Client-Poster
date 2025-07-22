package com.example.telegramclientposter.telegram.service;

import com.example.telegramclientposter.ollama.dto.BaseTelegramMessageDto;
import com.example.telegramclientposter.ollama.dto.OllamaTelegramPhotoMessageDto;
import com.example.telegramclientposter.ollama.dto.OllamaTelegramTextMessageDto;
import com.example.telegramclientposter.ollama.dto.OllamaTelegramVideoMessageDto;
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
    public void sendMessageToTelegram(BaseTelegramMessageDto dto) {
        log.info("Try send PROCESSED message to telegram");

        String processedText = dto.getProcessedText();
        long chatId = dto.getChatId();

        if (processedText == null || processedText.trim().isEmpty()) {
            log.warn("Processed text is empty for DTO: {}. Skipping message sending.", dto);
            return;
        }

        telegramClient.send(new TdApi.ParseTextEntities(processedText, new TdApi.TextParseModeMarkdown()))
                .whenComplete((formattedTextResult, throwable) -> {
                    if (throwable != null) {
                        log.error("Error parsing text entities for chat ID {}: {}", chatId, throwable.getMessage(), throwable);
                        return; // Важно выйти, если парсинг не удался
                    }
                    sendAppropriateTelegramMessage(dto, formattedTextResult, chatId);
                });
    }

    private void sendAppropriateTelegramMessage(BaseTelegramMessageDto dto, TdApi.FormattedText formattedText, long chatId) {
        TdApi.InputMessageContent messageContent = null;

        if (dto instanceof OllamaTelegramPhotoMessageDto photoMessageDto) {
            // Убедимся, что список fileIds не пустой
            if (photoMessageDto.getFileIds() != null && !photoMessageDto.getFileIds().isEmpty()) {
                int fileId = photoMessageDto.getFileIds().get(0);
                TdApi.InputMessagePhoto messagePhoto = new TdApi.InputMessagePhoto();
                messagePhoto.photo = new TdApi.InputFileId(fileId);
                messagePhoto.caption = formattedText;
                messageContent = messagePhoto;
            } else {
                log.warn("No file IDs found for photo message DTO: {}. Sending as text message.", dto);
                // Если нет фото, можно отправить просто текст
                messageContent = new TdApi.InputMessageText(formattedText, null, true);
            }
        } else if (dto instanceof OllamaTelegramTextMessageDto) {
            TdApi.InputMessageText messageText = new TdApi.InputMessageText();
            messageText.text = formattedText;
            messageContent = messageText;
        } else if (dto instanceof OllamaTelegramVideoMessageDto videoMessageDto) {
            // убеждаемся что есть файл ИД
            if (videoMessageDto.getFileIds() != null && !videoMessageDto.getFileIds().isEmpty()) {
                int fileId = videoMessageDto.getFileIds().get(0);
                TdApi.InputMessageVideo messageVideo = new TdApi.InputMessageVideo();
                messageVideo.video = new TdApi.InputFileId(fileId);
                messageVideo.caption = formattedText;
                messageContent = messageVideo;
            } else {
                log.warn("No file IDs found for video message DTO: {}. Sending as text", dto);
                messageContent = new TdApi.InputMessageText(formattedText, null, true);
            }
        } else {
            log.warn("Unsupported message DTO type: {}. Cannot send message to Telegram.", dto.getClass().getSimpleName());
            return; // Выходим, если тип DTO не поддерживается
        }

        TdApi.SendMessage sendMessage = new TdApi.SendMessage();
        sendMessage.chatId = chatId;
        sendMessage.inputMessageContent = messageContent;

        telegramClient.send(sendMessage, result -> {
            if (result.isError()) {
                log.error("Error sending message to chat ID {}: {}", chatId, result.getError().message);
            } else {
                log.info("Message sent successfully to chat ID {}", chatId);
            }
        });
    }
}
