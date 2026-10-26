package com.earmarked.earmark.messaging;

import com.earmarked.earmark.service.EarmarkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDeletedListener {

    private static final String USER_DELETED_TOPIC = "user.deleted";
    private final EarmarkService earmarkService;

    @KafkaListener(topics = USER_DELETED_TOPIC)
    public void onUserDeleted(String userId) {
        earmarkService.deleteAllForUser(UUID.fromString(userId));
    }
}
