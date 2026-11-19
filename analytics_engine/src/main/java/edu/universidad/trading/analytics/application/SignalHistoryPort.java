package edu.universidad.trading.analytics.application;

import edu.universidad.trading.analytics.domain.TradingSignal;

public interface SignalHistoryPort {
    void save(TradingSignal signal);
}
