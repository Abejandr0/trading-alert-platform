package edu.universidad.trading.analytics.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

interface TickJpaRepository extends JpaRepository<TickEntity, Long> {
}
