package edu.universidad.trading.analytics.application;

import edu.universidad.trading.contracts.SignalPayloadRecord;

public interface SignalOutboxPort {
    void enqueue(SignalPayloadRecord payload);
}
