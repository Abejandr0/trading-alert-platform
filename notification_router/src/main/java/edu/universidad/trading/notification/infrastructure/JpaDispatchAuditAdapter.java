package edu.universidad.trading.notification.infrastructure;

import edu.universidad.trading.contracts.SignalPayloadRecord;
import edu.universidad.trading.notification.application.DispatchAuditPort;
import edu.universidad.trading.notification.application.DispatchChannel;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
final class JpaDispatchAuditAdapter implements DispatchAuditPort {
    private final NotificationDispatchRepository repository;

    JpaDispatchAuditAdapter(NotificationDispatchRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean claim(SignalPayloadRecord signal, DispatchChannel channel) {
        int inserted = repository.insertIfAbsent(signal.eventId(), channel.name(),
                signal.assetCode().value(), signal.signal().name(), signal.timestamp());
        return inserted == 1
                || repository.existsByEventIdAndChannelAndDispatchedAtIsNull(signal.eventId(), channel.name());
    }

    @Override
    public void markDispatched(SignalPayloadRecord signal, DispatchChannel channel) {
        int updated = repository.markDispatched(signal.eventId(), channel.name(), Instant.now());
        if (updated != 1) {
            throw new IllegalStateException("dispatch claim is missing");
        }
    }
}
