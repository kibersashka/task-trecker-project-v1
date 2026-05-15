package com.task.tracker.aiimpl.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "groq")
public class GroqProperties {
    private String apiKey;
    private String baseUrl;
    private String model;
    private int maxTokens;
    private String version;
    private int timeoutSeconds;
}
