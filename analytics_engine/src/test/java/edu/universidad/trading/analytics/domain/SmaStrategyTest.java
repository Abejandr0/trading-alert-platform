package edu.universidad.trading.analytics.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class SmaStrategyTest {

    private final SmaStrategy strategy = new SmaStrategy(5);

    @Test
    void waitsUntilFivePricesAreAvailable() {
        assertTrue(strategy.calculate(List.of(BigDecimal.ONE, new BigDecimal("2"))).isEmpty());
    }

    @Test
    void calculatesMeanOfLastFivePrices() {
        var prices = List.of(new BigDecimal("999"), new BigDecimal("100"), new BigDecimal("102"),
                new BigDecimal("104"), new BigDecimal("106"), new BigDecimal("108"));

        assertEquals(0, strategy.calculate(prices).orElseThrow().compareTo(new BigDecimal("104")));
    }

    @Test
    void rejectsInvalidPeriodAndPrices() {
        assertThrows(IllegalArgumentException.class, () -> new SmaStrategy(0));
        assertThrows(IllegalArgumentException.class, () -> strategy.calculate(null));
        assertThrows(IllegalArgumentException.class,
                () -> strategy.calculate(List.of(BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ONE,
                        BigDecimal.ONE, BigDecimal.ONE)));
    }
}
