package edu.universidad.trading.contracts;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.UUID;

/** Contrato de publicación de una señal, independiente del dominio y del transporte. */
public record SignalPayloadRecord(
        @JsonProperty("asset") @JsonAlias("assetCode") AssetCode assetCode,
        SignalType signal, String reason, Instant timestamp, UUID eventId) {

    public SignalPayloadRecord {
        if (assetCode == null || signal == null || timestamp == null || reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("signal fields must be present");
        }
        if (eventId == null) {
            eventId = UUID.randomUUID();
        }
    }

    public SignalPayloadRecord(AssetCode assetCode, SignalType signal, String reason, Instant timestamp) {
        this(assetCode, signal, reason, timestamp, UUID.randomUUID());
    }
}
