package com.example.telegramclientposter.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter; // Import Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.example.telegramclientposter.constanta.Constants.*;

@Configuration
public class RabbitMQConfig {


    // Define the JSON MessageConverter as a Spring Bean
    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        // Отключаем создание messageId заголовков
        converter.setCreateMessageIds(false);
        return converter;
    }

    // Override the default RabbitTemplate to use our JSON converter
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter jsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        return rabbitTemplate;
    }

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