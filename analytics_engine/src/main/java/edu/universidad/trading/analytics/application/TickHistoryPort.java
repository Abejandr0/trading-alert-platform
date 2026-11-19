package edu.universidad.trading.analytics.application;

import edu.universidad.trading.contracts.TickPriceRecord;

public interface TickHistoryPort {
    void save(TickPriceRecord tick);
}
