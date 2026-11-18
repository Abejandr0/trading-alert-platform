package edu.universidad.trading.analytics.domain;

import edu.universidad.trading.contracts.AssetCode;
import edu.universidad.trading.contracts.TickPriceRecord;
import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

/** Agregado que conserva la ventana de precios y gobierna las transiciones de alerta. */
public final class AssetTracker {

    private final AssetCode assetCode;
    private final IndicatorStrategy indicator;
    private final Deque<BigDecimal> recentPrices = new ArrayDeque<>();
    private AssetState state = new NormalState();

    public AssetTracker(AssetCode assetCode, IndicatorStrategy indicator) {
        if (assetCode == null || indicator == null || indicator.requiredSamples() <= 0) {
            throw new IllegalArgumentException("tracker requires an asset and a valid indicator");
        }
        this.assetCode = assetCode;
        this.indicator = indicator;
    }

    public Optional<TradingSignal> registrarPrecio(TickPriceRecord tick) {
        if (tick == null || !assetCode.equals(tick.assetCode())) {
            throw new IllegalArgumentException("tick belongs to a different or missing asset");
        }
        recentPrices.addLast(tick.price());
        if (recentPrices.size() > indicator.requiredSamples()) {
            recentPrices.removeFirst();
        }
        return evaluateCurrentState(tick, indicator.calculate(List.copyOf(recentPrices)));
    }

    Optional<TradingSignal> evaluateCurrentState(TickPriceRecord tick, Optional<BigDecimal> value) {
        return state.onPrice(this, tick, value);
    }

    void transitionTo(AssetState nextState) {
        state = nextState;
    }

    AssetState currentState() {
        return state;
    }
}
