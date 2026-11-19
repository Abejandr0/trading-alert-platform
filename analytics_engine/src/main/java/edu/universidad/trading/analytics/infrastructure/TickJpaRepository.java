package edu.universidad.trading.analytics.infrastructure;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface TickJpaRepository extends JpaRepository<TickEntity, Long> {
    @Modifying
    @Query(value = """
            INSERT INTO ticks (asset_code, price, timestamp)
            VALUES (:assetCode, :price, :timestamp)
            ON CONFLICT (asset_code, timestamp) DO NOTHING
            """, nativeQuery = true)
    int insertIfAbsent(
            @Param("assetCode") String assetCode,
            @Param("price") BigDecimal price,
            @Param("timestamp") Instant timestamp);

    List<TickEntity> findTop4ByAssetCodeOrderByTimestampDesc(String assetCode);

    long countByAssetCodeAndTimestampAfter(String assetCode, Instant timestamp);
}
