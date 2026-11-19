package edu.universidad.trading.analytics.application;

import edu.universidad.trading.analytics.domain.TradingSignal;
import edu.universidad.trading.contracts.AssetCode;
import java.time.Instant;
import java.util.Optional;

public interface SignalHistoryPort {
    void save(TradingSignal signal);

    Optional<Instant> latestTimestamp(AssetCode assetCode);
}
