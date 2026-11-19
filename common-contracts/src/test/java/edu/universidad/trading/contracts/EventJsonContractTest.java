package edu.universidad.trading.contracts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class EventJsonContractTest {
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void publishesAssetAsTextInTickAndSignalEvents() throws Exception {
        AssetCode btc = new AssetCode("BTC");
        Instant time = Instant.parse("2026-01-01T00:00:00Z");

        var tick = mapper.readTree(mapper.writeValueAsString(
                new TickPriceRecord(btc, new BigDecimal("60000.00"), time)));
        var signal = mapper.readTree(mapper.writeValueAsString(
                new SignalPayloadRecord(btc, SignalType.BUY, "Momentum Crossover", time)));

        assertEquals("BTC", tick.get("asset").asText());
        assertEquals("BTC", signal.get("asset").asText());
        assertEquals("BUY", signal.get("signal").asText());
        assertFalse(tick.has("assetCode"));
    }

    @Test
    void readsPreviousNestedAssetCodeFormat() throws Exception {
        String previous = """
                {"assetCode":{"value":"BTC"},"price":60000,"timestamp":"2026-01-01T00:00:00Z"}
                """;

        TickPriceRecord tick = mapper.readValue(previous, TickPriceRecord.class);

        assertEquals(new AssetCode("BTC"), tick.assetCode());
    }

    @Test
    void readsCurrentFlatAssetFormat() throws Exception {
        String current = """
                {"asset":"BTC","signal":"BUY","reason":"Momentum Crossover", "timestamp":"2026-01-01T00:00:00Z"}
                """;

        SignalPayloadRecord signal = mapper.readValue(current, SignalPayloadRecord.class);

        assertEquals(new AssetCode("BTC"), signal.assetCode());
        assertEquals(SignalType.BUY, signal.signal());
    }
}
