package edu.universidad.trading.console.infrastructure;

import edu.universidad.trading.console.application.ConsoleAlertSink;
import edu.universidad.trading.contracts.SignalPayloadRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public final class LoggingConsoleAlertSink implements ConsoleAlertSink {
    private static final Logger LOG = LoggerFactory.getLogger(LoggingConsoleAlertSink.class);

    @Override
    public void display(SignalPayloadRecord signal) {
        LOG.warn("*** ALERTA DE SISTEMA: SEÑAL DE {} EN {} ***",
                signal.signal(), signal.assetCode().value());
    }
}
