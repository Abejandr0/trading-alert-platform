package edu.universidad.trading.feed.application;

import edu.universidad.trading.contracts.TickPriceRecord;

public interface TickPublisher {
    void publish(TickPriceRecord tick, String routingKey);
}
