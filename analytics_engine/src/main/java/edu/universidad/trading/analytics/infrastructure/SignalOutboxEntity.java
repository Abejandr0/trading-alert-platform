package edu.universidad.trading.analytics.infrastructure;

import edu.universidad.trading.contracts.SignalPayloadRecord;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "signal_outbox")
class SignalOutboxEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "asset_code", nullable = false, length = 21)
    private String assetCode;

    @Column(name = "payload_json", nullable = false, columnDefinition = "text")
    private String payloadJson;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "published_at")
    private Instant publishedAt;

    protected SignalOutboxEntity() {
    }

    SignalOutboxEntity(SignalPayloadRecord payload, String json) {
        this.assetCode = payload.assetCode().value();
        this.payloadJson = json;
        this.createdAt = Instant.now();
    }

    String assetCode() {
        return assetCode;
    }

    String payloadJson() {
        return payloadJson;
    }

    void markPublished(Instant time) {
        if (publishedAt != null) {
            throw new IllegalStateException("outbox event already published");
        }
        publishedAt = time;
    }
}
