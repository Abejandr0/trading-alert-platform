package edu.universidad.trading.bridge.application;

import edu.universidad.trading.contracts.TickPriceRecord;

public interface HistoricalTickPublisher {
    void publish(TickPriceRecord tick);
}
