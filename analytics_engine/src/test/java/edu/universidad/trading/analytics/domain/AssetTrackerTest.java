package edu.universidad.trading.analytics.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.universidad.trading.contracts.AssetCode;
import edu.universidad.trading.contracts.TickPriceRecord;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class AssetTrackerTest {

    private final AssetCode btc = new AssetCode("BTC");
    private final Instant start = Instant.parse("2026-01-01T00:00:00Z");

    @Test
    void emitsBuyOnlyAfterFivePricesAndMomentumCross() {
        AssetTracker tracker = new AssetTracker(btc, new SmaStrategy(5));

        for (int i = 0; i < 4; i++) {
            assertTrue(tracker.registrarPrecio(tick(i, "100")).isEmpty());
        }
        TradingSignal signal = tracker.registrarPrecio(tick(4, "130")).orElseThrow();

        assertEquals(btc, signal.assetCode());
        assertEquals(new BigDecimal("130"), signal.price());
        assertEquals(0, signal.sma().compareTo(new BigDecimal("106")));
        assertInstanceOf(AlertFiredState.class, tracker.currentState());
    }

    @Test
    void ignoresNormalPricesAndRejectsOtherAssets() {
        AssetTracker tracker = new AssetTracker(btc, new SmaStrategy(5));

        for (int i = 0; i < 6; i++) {
            assertTrue(tracker.registrarPrecio(tick(i, "100")).isEmpty());
        }

        assertInstanceOf(NormalState.class, tracker.currentState());
        assertThrows(IllegalArgumentException.class, () -> tracker.registrarPrecio(
                new TickPriceRecord(new AssetCode("ETH"), BigDecimal.ONE, start.plusSeconds(7))));
    }

    @Test
    void suppressesExactlyTenFollowingTicksThenAllowsNewAlert() {
        AssetTracker tracker = new AssetTracker(btc, new SmaStrategy(5));
        for (int i = 0; i < 4; i++) {
            tracker.registrarPrecio(tick(i, "100"));
        }
        assertTrue(tracker.registrarPrecio(tick(4, "130")).isPresent());

        for (int i = 5; i < 15; i++) {
            // Cada precio duplica el anterior y supera ampliamente el 5 % sobre la SMA.
            assertFalse(tracker.registrarPrecio(tick(i, Long.toString(200L << (i - 5)))).isPresent());
        }
        assertInstanceOf(NormalState.class, tracker.currentState());
        assertTrue(tracker.registrarPrecio(tick(15, "204800")).isPresent());
    }

    private TickPriceRecord tick(int seconds, String price) {
        return new TickPriceRecord(btc, new BigDecimal(price), start.plusSeconds(seconds));
    }
}
