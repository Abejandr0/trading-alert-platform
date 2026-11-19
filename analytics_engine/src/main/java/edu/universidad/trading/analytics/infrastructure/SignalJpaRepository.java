package edu.universidad.trading.analytics.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

interface SignalJpaRepository extends JpaRepository<SignalEntity, Long> {
}
