package edu.universidad.trading.analytics.infrastructure;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface SignalJpaRepository extends JpaRepository<SignalEntity, Long> {
    Optional<SignalEntity> findTopByAssetCodeOrderByTimestampDesc(String assetCode);
}
