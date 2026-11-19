package edu.universidad.trading.analytics.application;

import edu.universidad.trading.contracts.TickPriceRecord;
import edu.universidad.trading.contracts.AssetCode;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface TickHistoryPort {
    boolean saveIfAbsent(TickPriceRecord tick);

    /** Hasta cuatro precios anteriores en orden cronológico. */
    List<BigDecimal> recentPrices(AssetCode assetCode);

    long countSince(AssetCode assetCode, Instant timestamp);
}
