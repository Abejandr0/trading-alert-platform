package edu.universidad.trading.analytics.domain;

import edu.universidad.trading.contracts.TickPriceRecord;
import java.math.BigDecimal;
import java.util.Optional;

sealed interface AssetState permits NormalState, MomentumHighState, AlertFiredState {
    Optional<TradingSignal> onPrice(AssetTracker tracker, TickPriceRecord tick, Optional<BigDecimal> indicator);
}
