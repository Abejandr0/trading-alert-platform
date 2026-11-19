package edu.universidad.trading.analytics.infrastructure;

import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Reintenta publicaciones pendientes; el consumidor debe tolerar entrega al menos una vez. */
@Component
public final class KafkaSignalOutboxPublisher {
    private final OutboxJpaRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaSignalOutboxPublisher(
            OutboxJpaRepository repository, KafkaTemplate<String, String> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelayString = "${outbox.poll-ms:1000}")
    public void publishPending() {
        for (SignalOutboxEntity event : repository.findTop50ByPublishedAtIsNullOrderByCreatedAtAsc()) {
            try {
                kafkaTemplate.send("trading_signals", event.assetCode(), event.payloadJson())
                        .get(10, TimeUnit.SECONDS);
                event.markPublished(Instant.now());
                repository.save(event);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("outbox publication interrupted", ex);
            } catch (ExecutionException | TimeoutException ex) {
                throw new IllegalStateException("outbox publication failed", ex);
            }
        }
    }
}
