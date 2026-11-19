package edu.universidad.trading.analytics.infrastructure;

import edu.universidad.trading.analytics.application.SignalHistoryPort;
import edu.universidad.trading.analytics.domain.TradingSignal;
import edu.universidad.trading.contracts.AssetCode;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
final class JpaSignalHistoryAdapter implements SignalHistoryPort {
    private final SignalJpaRepository repository;

    JpaSignalHistoryAdapter(SignalJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(TradingSignal signal) {
        repository.save(new SignalEntity(signal));
    }

    @Override
    public Optional<Instant> latestTimestamp(AssetCode assetCode) {
        return repository.findTopByAssetCodeOrderByTimestampDesc(assetCode.value())
                .map(SignalEntity::timestamp);
    }
}
