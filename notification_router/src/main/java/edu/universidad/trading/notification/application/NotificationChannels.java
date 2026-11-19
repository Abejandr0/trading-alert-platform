package edu.universidad.trading.notification.application;

import edu.universidad.trading.contracts.SignalPayloadRecord;

public interface NotificationChannels {
    void enqueueEmail(SignalPayloadRecord signal);

    void broadcastConsole(SignalPayloadRecord signal);
}
