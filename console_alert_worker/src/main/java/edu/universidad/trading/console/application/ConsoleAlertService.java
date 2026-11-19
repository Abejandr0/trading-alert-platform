package edu.universidad.trading.console.application;

import edu.universidad.trading.contracts.SignalPayloadRecord;
import org.springframework.stereotype.Service;

@Service
public final class ConsoleAlertService {
    private final ConsoleAlertSink sink;

    public ConsoleAlertService(ConsoleAlertSink sink) {
        this.sink = sink;
    }

    public void onSignal(SignalPayloadRecord signal) {
        sink.display(signal);
    }
}
