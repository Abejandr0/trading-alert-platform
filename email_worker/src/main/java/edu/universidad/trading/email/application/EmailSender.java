package edu.universidad.trading.email.application;

import edu.universidad.trading.contracts.SignalPayloadRecord;

public interface EmailSender {
    void send(SignalPayloadRecord signal);
}
