package edu.universidad.trading.investment.application;

import edu.universidad.trading.contracts.SignalPayloadRecord;
import org.springframework.stereotype.Service;

@Service
public final class InvestmentDashboardService {
    private final SignalBook signalBook;
    private final InvestmentSignalPublisher publisher;

    public InvestmentDashboardService(SignalBook signalBook, InvestmentSignalPublisher publisher) {
        this.signalBook = signalBook;
        this.publisher = publisher;
    }

    public void onSignal(SignalPayloadRecord signal) {
        signalBook.record(signal);
        publisher.publish(signal);
    }
}
