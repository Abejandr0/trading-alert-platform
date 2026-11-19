package edu.universidad.trading.investment.application;

import edu.universidad.trading.contracts.SignalPayloadRecord;

public interface InvestmentSignalPublisher {
    void publish(SignalPayloadRecord signal);
}
