package edu.universidad.trading.contracts;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.Instant;

/** Evento de precio inmutable compartido entre los dos caminos de datos. */
public record TickPriceRecord(
        @JsonProperty("asset") @JsonAlias("assetCode") AssetCode assetCode,
        BigDecimal price,
        Instant timestamp) {

    public TickPriceRecord {
        if (assetCode == null || price == null || timestamp == null) {
            throw new IllegalArgumentException("tick fields must not be null");
        }
        if (price.signum() <= 0) {
            throw new IllegalArgumentException("tick price must be positive");
        }
    }
}
