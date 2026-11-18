package edu.universidad.trading.analytics.domain;

import edu.universidad.trading.contracts.TickPriceRecord;
import java.math.BigDecimal;
import java.util.Optional;

final class NormalState implements AssetState {

    @Override
    public Optional<TradingSignal> onPrice(
            AssetTracker tracker, TickPriceRecord tick, Optional<BigDecimal> indicator) {
        if (indicator.filter(sma -> MomentumRule.isHigh(tick.price(), sma)).isPresent()) {
            tracker.transitionTo(new MomentumHighState());
            return tracker.evaluateCurrentState(tick, indicator);
        }
        return Optional.empty();
    }
}
