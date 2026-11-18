package edu.universidad.trading.analytics.domain;

import java.math.BigDecimal;

final class MomentumRule {

    private static final BigDecimal BUY_MULTIPLIER = new BigDecimal("1.05");

    private MomentumRule() {
    }

    static boolean isHigh(BigDecimal price, BigDecimal sma) {
        return price.compareTo(sma.multiply(BUY_MULTIPLIER)) > 0;
    }
}
