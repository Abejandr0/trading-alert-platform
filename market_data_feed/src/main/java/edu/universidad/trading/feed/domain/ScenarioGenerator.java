package edu.universidad.trading.feed.domain;

import edu.universidad.trading.contracts.AssetCode;
import edu.universidad.trading.contracts.TickPriceRecord;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/** Genera ruido acotado alrededor del precio base y picos periódicos reproducibles. */
public final class ScenarioGenerator {
    private final Random random;
    private final Map<AssetCode, Long> counts = new HashMap<>();

    public ScenarioGenerator(long seed) {
        this.random = new Random(seed);
    }

    public synchronized TickPriceRecord next(MarketScenario scenario, Instant timestamp) {
        if (scenario == null || timestamp == null) {
            throw new IllegalArgumentException("scenario and timestamp are required");
        }
        long count = counts.merge(scenario.assetCode(), 1L, Long::sum);
        BigDecimal variation = BigDecimal.valueOf(random.nextDouble() * 2.0 - 1.0)
                .multiply(scenario.volatilityPercent())
                .movePointLeft(2);
        BigDecimal price = scenario.basePrice().multiply(BigDecimal.ONE.add(variation));
        if (count % scenario.spikeEveryTicks() == 0) {
            price = price.multiply(scenario.spikeMultiplier());
        }
        return new TickPriceRecord(scenario.assetCode(), price.setScale(8, RoundingMode.HALF_UP), timestamp);
    }
}
