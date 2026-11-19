package edu.universidad.trading.notification.application;

import edu.universidad.trading.contracts.SignalPayloadRecord;

public interface DispatchAuditPort {
    boolean claim(SignalPayloadRecord signal, DispatchChannel channel);

    void markDispatched(SignalPayloadRecord signal, DispatchChannel channel);
}
