package edu.universidad.trading.contracts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class SignalPayloadRecordTest {

    private final AssetCode btc = new AssetCode("BTC");
    private final Instant time = Instant.parse("2026-01-01T00:00:00Z");

    @Test
    void storesValidatedBuySignal() {
        SignalPayloadRecord payload = new SignalPayloadRecord(btc, SignalType.BUY, "Momentum Crossover", time);

        assertEquals(SignalType.BUY, payload.signal());
        assertEquals("Momentum Crossover", payload.reason());
    }

    @Test
    void rejectsIncompleteSignal() {
        assertThrows(IllegalArgumentException.class, () -> new SignalPayloadRecord(null, SignalType.BUY, "Momentum", time));
        assertThrows(IllegalArgumentException.class, () -> new SignalPayloadRecord(btc, null, "Momentum", time));
        assertThrows(IllegalArgumentException.class, () -> new SignalPayloadRecord(btc, SignalType.BUY, " ", time));
        assertThrows(IllegalArgumentException.class, () -> new SignalPayloadRecord(btc, SignalType.BUY, "Momentum", null));
    }
}
