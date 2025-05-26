package com.example.telegramclientposter.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties("telegram")
public class ApplicationProperties {
    private int apiId;
    private String apiHash;
    private String sessionName;
    private String phone;
}
