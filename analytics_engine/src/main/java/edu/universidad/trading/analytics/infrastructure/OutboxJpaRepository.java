package edu.universidad.trading.analytics.infrastructure;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

interface OutboxJpaRepository extends JpaRepository<SignalOutboxEntity, Long> {
    List<SignalOutboxEntity> findTop50ByPublishedAtIsNullOrderByCreatedAtAsc();
}
