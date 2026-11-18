package edu.universidad.trading.contracts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class TickPriceRecordTest {

    private final AssetCode btc = new AssetCode("BTC");
    private final Instant time = Instant.parse("2026-01-01T00:00:00Z");

    @Test
    void preservesFinancialValueWithoutFloatingPointConversion() {
        TickPriceRecord tick = new TickPriceRecord(btc, new BigDecimal("60000.12345678"), time);

        assertEquals(new BigDecimal("60000.12345678"), tick.price());
        assertEquals(btc, tick.assetCode());
        assertEquals(time, tick.timestamp());
    }

    @Test
    void rejectsNonPositivePriceAndMissingFields() {
        assertThrows(IllegalArgumentException.class, () -> new TickPriceRecord(btc, BigDecimal.ZERO, time));
        assertThrows(IllegalArgumentException.class, () -> new TickPriceRecord(btc, new BigDecimal("-1"), time));
        assertThrows(IllegalArgumentException.class, () -> new TickPriceRecord(null, BigDecimal.ONE, time));
        assertThrows(IllegalArgumentException.class, () -> new TickPriceRecord(btc, null, time));
        assertThrows(IllegalArgumentException.class, () -> new TickPriceRecord(btc, BigDecimal.ONE, null));
    }
}
