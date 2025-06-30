package com.example.telegramclientposter.constanta;

public class Constants {
    // Название очереди, куда DuplicateCheckerService будет отправлять сообщения для Ollama
    public static final String OLLAMA_VALID_QUEUE_NAME = "ollama-processing-queue";
    public static final String OLLAMA_VALID_EXCHANGE_NAME = "ollama-processing-exchange";

    // Название очереди, куда OllamaService будет отправлять обработанные сообщения для отправки в Telegram
    public static final String TELEGRAM_SEND_QUEUE_NAME = "telegram-send-queue";
    public static final String TELEGRAM_SEND_EXCHANGE_NAME = "telegram-send-exchange";

    // название очереди, куда ChannelMessageListener будет слать для проверки на дубли DuplicateCheckerService
    public static final String OLLAMA_EMBEDDING_QUEUE_NAME = "ollama-embedding-queue";
    public static final String OLLAMA_EMBEDDING_EXCHANGE_NAME = "ollama-embedding-exchange";

    // settings for duplication checker
    public static final double SIMILARITY_THRESHOLD = 0.63;
    public static final int NUMBER_OF_MESSAGES_TO_COMPARE = 10;
}
