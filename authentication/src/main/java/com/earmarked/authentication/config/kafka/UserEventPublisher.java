package com.earmarked.authentication.config.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publishUserDeleted(UUID userId) {
        String id = userId.toString();
        kafkaTemplate.send(KafkaTopicConfig.USER_DELETED_TOPIC, id, id);
    }
}
