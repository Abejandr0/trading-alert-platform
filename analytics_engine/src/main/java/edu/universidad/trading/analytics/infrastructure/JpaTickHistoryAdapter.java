package edu.universidad.trading.analytics.infrastructure;

import edu.universidad.trading.analytics.application.TickHistoryPort;
import edu.universidad.trading.contracts.AssetCode;
import edu.universidad.trading.contracts.TickPriceRecord;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
final class JpaTickHistoryAdapter implements TickHistoryPort {
    private final TickJpaRepository repository;

    JpaTickHistoryAdapter(TickJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean saveIfAbsent(TickPriceRecord tick) {
        return repository.insertIfAbsent(tick.assetCode().value(), tick.price(), tick.timestamp()) == 1;
    }

    @Override
    public List<BigDecimal> recentPrices(AssetCode assetCode) {
        List<BigDecimal> prices = new ArrayList<>(repository.findTop4ByAssetCodeOrderByTimestampDesc(assetCode.value())
                .stream().map(TickEntity::price).toList());
        Collections.reverse(prices);
        return List.copyOf(prices);
    }

    @Override
    public long countSince(AssetCode assetCode, Instant timestamp) {
        return repository.countByAssetCodeAndTimestampAfter(assetCode.value(), timestamp);
    }
}
