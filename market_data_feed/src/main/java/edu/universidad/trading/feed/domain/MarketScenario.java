package edu.universidad.trading.feed.domain;

import edu.universidad.trading.contracts.AssetCode;
import java.math.BigDecimal;
import java.util.Locale;

public record MarketScenario(
        AssetCode assetCode,
        String market,
        BigDecimal basePrice,
        BigDecimal volatilityPercent,
        int spikeEveryTicks,
        BigDecimal spikeMultiplier) {

    public MarketScenario {
        if (assetCode == null || market == null || !market.matches("[a-z]+")
                || basePrice == null || basePrice.signum() <= 0
                || volatilityPercent == null || volatilityPercent.signum() < 0
                || volatilityPercent.compareTo(new BigDecimal("100")) >= 0
                || spikeEveryTicks <= 0
                || spikeMultiplier == null || spikeMultiplier.compareTo(BigDecimal.ONE) <= 0) {
            throw new IllegalArgumentException("invalid market scenario");
        }
    }

    public String routingKey() {
        return "ticks." + market + "." + assetCode.value().toLowerCase(Locale.ROOT).replace('/', '_');
    }
}
