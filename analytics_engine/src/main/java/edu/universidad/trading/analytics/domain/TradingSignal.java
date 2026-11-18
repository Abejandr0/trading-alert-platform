package edu.universidad.trading.analytics.domain;

import edu.universidad.trading.contracts.AssetCode;
import edu.universidad.trading.contracts.SignalPayloadRecord;
import edu.universidad.trading.contracts.SignalType;
import java.math.BigDecimal;
import java.time.Instant;

/** Señal de dominio con comportamiento de notificación y reglas de construcción. */
public final class TradingSignal {

    private static final String REASON = "Momentum Crossover";
    private final AssetCode assetCode;
    private final BigDecimal price;
    private final BigDecimal sma;
    private final Instant timestamp;
    private boolean notified;

    private TradingSignal(AssetCode assetCode, BigDecimal price, BigDecimal sma, Instant timestamp) {
        if (assetCode == null || price == null || sma == null || timestamp == null
                || price.signum() <= 0 || sma.signum() <= 0) {
            throw new IllegalArgumentException("signal requires positive prices and complete fields");
        }
        if (!MomentumRule.isHigh(price, sma)) {
            throw new IllegalArgumentException("BUY requires a price above 105% of SMA");
        }
        this.assetCode = assetCode;
        this.price = price;
        this.sma = sma;
        this.timestamp = timestamp;
    }

    public static TradingSignal buy(AssetCode assetCode, BigDecimal price, BigDecimal sma, Instant timestamp) {
        return new TradingSignal(assetCode, price, sma, timestamp);
    }

    public SignalPayloadRecord toPayload() {
        return new SignalPayloadRecord(assetCode, SignalType.BUY, REASON, timestamp);
    }

    public void marcarNotificada() {
        if (notified) {
            throw new IllegalStateException("signal already notified");
        }
        notified = true;
    }

    public boolean isNotified() {
        return notified;
    }

    public AssetCode assetCode() {
        return assetCode;
    }

    public BigDecimal price() {
        return price;
    }

    public BigDecimal sma() {
        return sma;
    }
}
