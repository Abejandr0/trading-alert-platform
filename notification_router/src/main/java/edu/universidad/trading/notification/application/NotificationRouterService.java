package edu.universidad.trading.notification.application;

import edu.universidad.trading.contracts.SignalPayloadRecord;
import org.springframework.stereotype.Service;

@Service
public final class NotificationRouterService {
    private final NotificationChannels channels;
    private final DispatchAuditPort audit;

    public NotificationRouterService(NotificationChannels channels, DispatchAuditPort audit) {
        this.channels = channels;
        this.audit = audit;
    }

    public void route(SignalPayloadRecord signal) {
        if (audit.claim(signal, DispatchChannel.EMAIL)) {
            channels.enqueueEmail(signal);
            audit.markDispatched(signal, DispatchChannel.EMAIL);
        }
        if (audit.claim(signal, DispatchChannel.CONSOLE)) {
            channels.broadcastConsole(signal);
            audit.markDispatched(signal, DispatchChannel.CONSOLE);
        }
    }
}
