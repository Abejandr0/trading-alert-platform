package edu.universidad.trading.analytics.domain;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.Optional;

/** Media móvil simple de los últimos {@code period} precios. */
public final class SmaStrategy implements IndicatorStrategy {

    private final int period;

    public SmaStrategy(int period) {
        if (period <= 0) {
            throw new IllegalArgumentException("period must be positive");
        }
        this.period = period;
    }

    @Override
    public int requiredSamples() {
        return period;
    }

    @Override
    public Optional<BigDecimal> calculate(List<BigDecimal> prices) {
        if (prices == null || prices.stream().anyMatch(price -> price == null || price.signum() <= 0)) {
            throw new IllegalArgumentException("prices must be present and positive");
        }
        if (prices.size() < period) {
            return Optional.empty();
        }
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = prices.size() - period; i < prices.size(); i++) {
            sum = sum.add(prices.get(i));
        }
        return Optional.of(sum.divide(BigDecimal.valueOf(period), MathContext.DECIMAL128));
    }
}
