package edu.universidad.trading.feed.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.universidad.trading.contracts.AssetCode;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class ScenarioGeneratorTest {
    @Test
    void spikesAtConfiguredIntervalAndThenRevertsToBaseline() {
        MarketScenario scenario = new MarketScenario(new AssetCode("BTC"), "crypto",
                new BigDecimal("100"), BigDecimal.ZERO, 3, new BigDecimal("1.25"));
        ScenarioGenerator generator = new ScenarioGenerator(42);
        Instant time = Instant.parse("2026-01-01T00:00:00Z");

        assertEquals(0, generator.next(scenario, time).price().compareTo(new BigDecimal("100")));
        assertEquals(0, generator.next(scenario, time).price().compareTo(new BigDecimal("100")));
        assertEquals(0, generator.next(scenario, time).price().compareTo(new BigDecimal("125")));
        assertEquals(0, generator.next(scenario, time).price().compareTo(new BigDecimal("100")));
    }

    @Test
    void rejectsInvalidScenario() {
        assertThrows(IllegalArgumentException.class, () -> new MarketScenario(
                new AssetCode("BTC"), "crypto", BigDecimal.ONE, new BigDecimal("100"), 3,
                new BigDecimal("1.25")));
    }
}
