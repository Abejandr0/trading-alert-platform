package edu.universidad.trading.notification.infrastructure;

import edu.universidad.trading.contracts.SignalPayloadRecord;
import edu.universidad.trading.notification.application.DispatchChannel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "notification_dispatches")
class NotificationDispatchEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "asset_code", nullable = false, length = 21)
    private String assetCode;

    @Column(name = "signal", nullable = false, length = 12)
    private String signal;

    @Column(name = "channel", nullable = false, length = 12)
    private String channel;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    @Column(name = "dispatched_at", nullable = false)
    private Instant dispatchedAt;

    protected NotificationDispatchEntity() {
    }

    NotificationDispatchEntity(SignalPayloadRecord payload, DispatchChannel channel) {
        this.assetCode = payload.assetCode().value();
        this.signal = payload.signal().name();
        this.channel = channel.name();
        this.timestamp = payload.timestamp();
        this.dispatchedAt = Instant.now();
    }
}
