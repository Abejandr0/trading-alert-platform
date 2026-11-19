package edu.universidad.trading.analytics.infrastructure;

import edu.universidad.trading.contracts.TickPriceRecord;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "ticks")
class TickEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "asset_code", nullable = false, length = 21)
    private String assetCode;

    @Column(name = "price", nullable = false, precision = 24, scale = 8)
    private BigDecimal price;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    protected TickEntity() {
    }

    TickEntity(TickPriceRecord tick) {
        this.assetCode = tick.assetCode().value();
        this.price = tick.price();
        this.timestamp = tick.timestamp();
    }

    BigDecimal price() {
        return price;
    }
}
