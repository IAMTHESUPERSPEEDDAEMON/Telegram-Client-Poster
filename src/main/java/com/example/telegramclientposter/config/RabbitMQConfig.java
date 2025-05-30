package com.example.telegramclientposter.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.example.telegramclientposter.constanta.Constants.*;

@Configuration
public class RabbitMQConfig {

    // 1. Очередь для Ollama
    @Bean
    public Queue ollamaQueue() {
        return new Queue(OLLAMA_QUEUE_NAME, true); // true = durable (сохраняется при перезапуске брокера)
    }

    @Bean
    public DirectExchange ollamaExchange() {
        return new DirectExchange(OLLAMA_EXCHANGE_NAME);
    }

    @Bean
    public Binding ollamaBinding(Queue ollamaQueue, DirectExchange ollamaExchange) {
        return BindingBuilder.bind(ollamaQueue).to(ollamaExchange).with(OLLAMA_QUEUE_NAME);
    }

    // 2. Очередь для отправки в Telegram
    @Bean
    public Queue telegramSendQueue() {
        return new Queue(TELEGRAM_SEND_QUEUE_NAME, true);
    }

    @Bean
    public DirectExchange telegramSendExchange() {
        return new DirectExchange(TELEGRAM_SEND_EXCHANGE_NAME);
    }

    @Bean
    public Binding telegramSendBinding(Queue telegramSendQueue, DirectExchange telegramSendExchange) {
        return BindingBuilder.bind(telegramSendQueue).to(telegramSendExchange).with(TELEGRAM_SEND_QUEUE_NAME);
    }
}