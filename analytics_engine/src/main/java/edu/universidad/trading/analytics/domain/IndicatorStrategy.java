package edu.universidad.trading.analytics.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/** Puerto de cálculo técnico que permite sustituir la fórmula sin cambiar el tracker. */
public interface IndicatorStrategy {
    int requiredSamples();

    Optional<BigDecimal> calculate(List<BigDecimal> prices);
}
