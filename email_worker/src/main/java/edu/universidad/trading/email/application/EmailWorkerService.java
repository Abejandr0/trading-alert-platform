package edu.universidad.trading.email.application;

import edu.universidad.trading.contracts.SignalPayloadRecord;
import org.springframework.stereotype.Service;

@Service
public final class EmailWorkerService {
    private final EmailSender sender;

    public EmailWorkerService(EmailSender sender) {
        this.sender = sender;
    }

    public void dispatch(SignalPayloadRecord signal) {
        sender.send(signal);
    }
}
