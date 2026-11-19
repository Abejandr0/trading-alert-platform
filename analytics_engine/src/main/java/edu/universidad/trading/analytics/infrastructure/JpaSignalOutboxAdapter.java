package edu.universidad.trading.analytics.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.universidad.trading.analytics.application.SignalOutboxPort;
import edu.universidad.trading.contracts.SignalPayloadRecord;
import org.springframework.stereotype.Component;

@Component
final class JpaSignalOutboxAdapter implements SignalOutboxPort {
    private final OutboxJpaRepository repository;
    private final ObjectMapper objectMapper;

    JpaSignalOutboxAdapter(OutboxJpaRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void enqueue(SignalPayloadRecord payload) {
        try {
            repository.save(new SignalOutboxEntity(payload, objectMapper.writeValueAsString(payload)));
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("cannot serialize signal", ex);
        }
    }
}
