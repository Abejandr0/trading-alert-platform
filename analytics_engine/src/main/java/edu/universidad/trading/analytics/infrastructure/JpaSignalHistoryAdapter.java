package edu.universidad.trading.analytics.infrastructure;

import edu.universidad.trading.analytics.application.SignalHistoryPort;
import edu.universidad.trading.analytics.domain.TradingSignal;
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
}
