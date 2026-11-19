package edu.universidad.trading.notification.application;

import edu.universidad.trading.contracts.SignalPayloadRecord;

public interface DispatchAuditPort {
    void record(SignalPayloadRecord signal, DispatchChannel channel);
}
