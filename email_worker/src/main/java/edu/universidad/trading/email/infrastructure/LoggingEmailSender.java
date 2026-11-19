package edu.universidad.trading.email.infrastructure;

import edu.universidad.trading.contracts.SignalPayloadRecord;
import edu.universidad.trading.email.application.EmailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public final class LoggingEmailSender implements EmailSender {
    private static final Logger LOG = LoggerFactory.getLogger(LoggingEmailSender.class);

    @Override
    public void send(SignalPayloadRecord signal) {
        LOG.info("ENVIANDO EMAIL: ¡Momento de invertir! Señal de {} para {}",
                signal.signal(), signal.assetCode().value());
    }
}
