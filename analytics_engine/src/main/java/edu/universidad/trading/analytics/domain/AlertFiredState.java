package edu.universidad.trading.analytics.domain;

import edu.universidad.trading.contracts.TickPriceRecord;
import java.math.BigDecimal;
import java.util.Optional;

final class AlertFiredState implements AssetState {

    private final int ticksRemaining;

    AlertFiredState(int ticksRemaining) {
        if (ticksRemaining <= 0) {
            throw new IllegalArgumentException("cooldown must be positive");
        }
        this.ticksRemaining = ticksRemaining;
    }

    @Override
    public Optional<TradingSignal> onPrice(
            AssetTracker tracker, TickPriceRecord tick, Optional<BigDecimal> indicator) {
        tracker.transitionTo(ticksRemaining == 1
                ? new NormalState()
                : new AlertFiredState(ticksRemaining - 1));
        return Optional.empty();
    }
}
