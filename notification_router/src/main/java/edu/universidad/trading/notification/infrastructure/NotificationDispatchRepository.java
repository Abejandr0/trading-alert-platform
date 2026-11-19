package edu.universidad.trading.notification.infrastructure;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

interface NotificationDispatchRepository extends JpaRepository<NotificationDispatchEntity, Long> {
    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO notification_dispatches
                (event_id, channel, asset_code, signal, timestamp, dispatched_at)
            VALUES (:eventId, :channel, :assetCode, :signal, :timestamp, NULL)
            ON CONFLICT (event_id, channel) DO NOTHING
            """, nativeQuery = true)
    int insertIfAbsent(
            @Param("eventId") UUID eventId,
            @Param("channel") String channel,
            @Param("assetCode") String assetCode,
            @Param("signal") String signal,
            @Param("timestamp") Instant timestamp);

    boolean existsByEventIdAndChannelAndDispatchedAtIsNull(UUID eventId, String channel);

    @Modifying
    @Transactional
    @Query("UPDATE NotificationDispatchEntity d SET d.dispatchedAt = :dispatchedAt "
            + "WHERE d.eventId = :eventId AND d.channel = :channel")
    int markDispatched(
            @Param("eventId") UUID eventId,
            @Param("channel") String channel,
            @Param("dispatchedAt") Instant dispatchedAt);
}
