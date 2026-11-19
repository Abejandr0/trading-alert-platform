package edu.universidad.trading.console.application;

import edu.universidad.trading.contracts.SignalPayloadRecord;

public interface ConsoleAlertSink {
    void display(SignalPayloadRecord signal);
}
