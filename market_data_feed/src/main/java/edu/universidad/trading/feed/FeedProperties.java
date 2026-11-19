package edu.universidad.trading.feed;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "feed")
public record FeedProperties(long seed, List<ScenarioSettings> scenarios) {
    public record ScenarioSettings(
            String assetCode,
            String market,
            BigDecimal basePrice,
            BigDecimal volatilityPercent,
            int spikeEveryTicks,
            BigDecimal spikeMultiplier) {
    }
}
