package com.earmarked.earmark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class EarmarkApplication {

    public static void main(String[] args) {
        SpringApplication.run(EarmarkApplication.class, args);
    }

}
