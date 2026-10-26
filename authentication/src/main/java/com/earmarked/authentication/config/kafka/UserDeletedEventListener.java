package com.earmarked.authentication.config.kafka;

import com.earmarked.authentication.dto.UserDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class UserDeletedEventListener {

    private final UserEventPublisher userEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserDeleted(UserDeletedEvent event) {
        userEventPublisher.publishUserDeleted(event.userId());
    }
}
