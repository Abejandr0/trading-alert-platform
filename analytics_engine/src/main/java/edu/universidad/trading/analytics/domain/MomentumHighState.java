package edu.universidad.trading.analytics.domain;

import edu.universidad.trading.contracts.TickPriceRecord;
import java.math.BigDecimal;
import java.util.Optional;

final class MomentumHighState implements AssetState {

    @Override
    public Optional<TradingSignal> onPrice(
            AssetTracker tracker, TickPriceRecord tick, Optional<BigDecimal> indicator) {
        TradingSignal signal = TradingSignal.buy(
                tick.assetCode(), tick.price(), indicator.orElseThrow(), tick.timestamp());
        tracker.transitionTo(new AlertFiredState(10));
        return Optional.of(signal);
    }
}
