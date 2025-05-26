package com.example.telegramclientposter.telegram.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.*;

@Getter
@Slf4j
@Component
public class ChannelConfigLoader {

    private final Set<Long> monitoredChannelIds;

    public ChannelConfigLoader() {
        this.monitoredChannelIds = loadChannelIds();
    }

    private Set<Long> loadChannelIds() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = new ClassPathResource("channels.json").getInputStream();
            JsonNode channels = mapper.readTree(inputStream);

            Set<Long> chatIds = new HashSet<>();
            for (JsonNode channel : channels) {
                String chatIdStr = channel.get("chatId").asText();
                try {
                    chatIds.add(Long.parseLong(chatIdStr));
                } catch (NumberFormatException e) {
                    log.warn("⚠️ Invalid chatId format in channels.json: {}", chatIdStr);
                }
            }

            log.info("✅ Loaded {} monitored channels", chatIds.size());
            return chatIds;
        } catch (Exception e) {
            log.error("❌ Failed to load channels.json", e);
            return Collections.emptySet();
        }
    }

    public boolean isMonitoredChannel(long chatId) {
        return monitoredChannelIds.contains(chatId);
    }

}

