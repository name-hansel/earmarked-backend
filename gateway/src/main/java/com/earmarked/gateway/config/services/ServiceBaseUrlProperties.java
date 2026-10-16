package com.earmarked.gateway.config.services;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "services")
public record ServiceBaseUrlProperties(String auth, String spotify, String earmark) {
}
