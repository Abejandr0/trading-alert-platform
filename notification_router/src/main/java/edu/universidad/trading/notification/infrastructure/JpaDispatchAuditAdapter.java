package edu.universidad.trading.notification.infrastructure;

import edu.universidad.trading.contracts.SignalPayloadRecord;
import edu.universidad.trading.notification.application.DispatchAuditPort;
import edu.universidad.trading.notification.application.DispatchChannel;
import org.springframework.stereotype.Component;

@Component
final class JpaDispatchAuditAdapter implements DispatchAuditPort {
    private final NotificationDispatchRepository repository;

    JpaDispatchAuditAdapter(NotificationDispatchRepository repository) {
        this.repository = repository;
    }

    @Override
    public void record(SignalPayloadRecord signal, DispatchChannel channel) {
        repository.save(new NotificationDispatchEntity(signal, channel));
    }
}
