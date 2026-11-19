package edu.universidad.trading.live.application;

import edu.universidad.trading.contracts.TickPriceRecord;

public interface LiveTickPublisher {
    void publish(TickPriceRecord tick);
}
