package edu.universidad.trading.analytics.infrastructure;

import edu.universidad.trading.analytics.domain.TradingSignal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "signals")
class SignalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "asset_code", nullable = false, length = 21)
    private String assetCode;

    @Column(name = "signal", nullable = false, length = 12)
    private String signal;

    @Column(name = "reason", nullable = false, length = 100)
    private String reason;

    @Column(name = "price", nullable = false, precision = 24, scale = 8)
    private BigDecimal price;

    @Column(name = "sma", nullable = false, precision = 24, scale = 8)
    private BigDecimal sma;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    protected SignalEntity() {
    }

    SignalEntity(TradingSignal tradingSignal) {
        var payload = tradingSignal.toPayload();
        this.assetCode = payload.assetCode().value();
        this.signal = payload.signal().name();
        this.reason = payload.reason();
        this.price = tradingSignal.price();
        this.sma = tradingSignal.sma();
        this.timestamp = payload.timestamp();
    }
}
