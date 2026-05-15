package com.task.tracker.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "services")
public class ServiceProperties {
    private String authUrl;
    private String taskUrl;
    private String userUrl;
    private String aiUrl;
    private String notificationUrl;
}











