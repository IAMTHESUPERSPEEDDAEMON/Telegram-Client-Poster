package com.example.telegramclientposter.telegram.client;

import com.example.telegramclientposter.config.ApplicationProperties;
import it.tdlight.Init;
import it.tdlight.Log;
import it.tdlight.Slf4JLogMessageHandler;
import it.tdlight.client.*;
import it.tdlight.util.UnsupportedNativeLibraryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Configuration
public class TelegramClientInitializer {

    private final ApplicationProperties properties;

    @Autowired
    public TelegramClientInitializer(ApplicationProperties properties) {
        this.properties = properties;
    }

    @Bean
    public SimpleTelegramClientFactory telegramClientFactory() {
        return new SimpleTelegramClientFactory();
    }

    @Bean
    public SimpleTelegramClient telegramClient(SimpleTelegramClientFactory clientFactory) throws UnsupportedNativeLibraryException {
        Init.init();
        Log.setLogMessageHandler(1, new Slf4JLogMessageHandler());

        var apiToken = new APIToken(properties.getApiId(), properties.getApiHash());
        TDLibSettings settings = TDLibSettings.create(apiToken);

        Path sessionPath = Paths.get("tdlib-session-" + properties.getSessionName());
        settings.setDatabaseDirectoryPath(sessionPath.resolve("data"));
        settings.setDownloadedFilesDirectoryPath(sessionPath.resolve("downloads"));

        var clientBuilder = clientFactory.builder(settings);

//        // add message listener
//        clientBuilder.addUpdateHandler(TdApi.UpdateNewMessage.class, telegramClientService::onUpdate);

        if (!sessionPath.resolve("data").toFile().exists()) {
            log.info("🔐 Session not exists — creating new one");
            return clientBuilder.build(AuthenticationSupplier.consoleLogin());
        } else {
            log.info("🔄 Using existing session");
            return clientBuilder.build(AuthenticationSupplier.user(properties.getPhone()));
        }
    }
}
