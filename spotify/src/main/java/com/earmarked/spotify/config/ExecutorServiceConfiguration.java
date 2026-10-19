package com.earmarked.spotify.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorServiceConfiguration {

    @Bean(destroyMethod = "close")
    public ExecutorService albumExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
