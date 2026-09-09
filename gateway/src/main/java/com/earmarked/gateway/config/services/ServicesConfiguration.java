package com.earmarked.gateway.config.services;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ServiceBaseUrlProperties.class)
public class ServicesConfiguration {
}
