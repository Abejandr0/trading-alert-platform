package edu.universidad.trading.analytics.infrastructure;

import edu.universidad.trading.analytics.application.TickHistoryPort;
import edu.universidad.trading.contracts.TickPriceRecord;
import org.springframework.stereotype.Component;

@Component
final class JpaTickHistoryAdapter implements TickHistoryPort {
    private final TickJpaRepository repository;

    JpaTickHistoryAdapter(TickJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(TickPriceRecord tick) {
        repository.save(new TickEntity(tick));
    }
}
