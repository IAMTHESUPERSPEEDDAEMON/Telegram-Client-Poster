package com.example.telegramclientposter.constanta;

public class Constants {
    // Название очереди, куда ChannelMessageListener будет отправлять сообщения для Ollama
    public static final String OLLAMA_QUEUE_NAME = "ollama-processing-queue";
    // Название обменника (можно использовать default, но для ясности создадим DirectExchange)
    public static final String OLLAMA_EXCHANGE_NAME = "ollama-exchange";

    // Название очереди, куда OllamaService будет отправлять обработанные сообщения для отправки в Telegram
    public static final String TELEGRAM_SEND_QUEUE_NAME = "telegram-sending-queue";
    public static final String TELEGRAM_SEND_EXCHANGE_NAME = "telegram-send-exchange";
}
