package edu.universidad.trading.analytics.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.universidad.trading.contracts.AssetCode;
import edu.universidad.trading.contracts.SignalType;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class TradingSignalTest {

    @Test
    void createsPayloadAndCanBeMarkedNotifiedOnlyOnce() {
        TradingSignal signal = TradingSignal.buy(new AssetCode("BTC"), new BigDecimal("130"),
                new BigDecimal("106"), Instant.parse("2026-01-01T00:00:00Z"));

        assertEquals(SignalType.BUY, signal.toPayload().signal());
        assertEquals("Momentum Crossover", signal.toPayload().reason());
        assertFalse(signal.isNotified());
        signal.marcarNotificada();
        assertTrue(signal.isNotified());
        assertThrows(IllegalStateException.class, signal::marcarNotificada);
    }

    @Test
    void rejectsSignalWithoutPositiveValues() {
        assertThrows(IllegalArgumentException.class, () -> TradingSignal.buy(new AssetCode("BTC"),
                BigDecimal.ZERO, BigDecimal.ONE, Instant.now()));
        assertThrows(IllegalArgumentException.class, () -> TradingSignal.buy(new AssetCode("BTC"),
                BigDecimal.ONE, BigDecimal.ZERO, Instant.now()));
        assertThrows(IllegalArgumentException.class, () -> TradingSignal.buy(new AssetCode("BTC"),
                new BigDecimal("105"), new BigDecimal("100"), Instant.now()));
    }
}
