package edu.universidad.trading.analytics.application;

import edu.universidad.trading.analytics.domain.AssetTracker;
import edu.universidad.trading.analytics.domain.IndicatorStrategy;
import edu.universidad.trading.contracts.AssetCode;
import edu.universidad.trading.contracts.TickPriceRecord;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/** Persiste cada tick y registra señales y publicaciones pendientes en una transacción. */
@Service
public class AnalyticsService {
    private final TickHistoryPort ticks;
    private final SignalHistoryPort signals;
    private final SignalOutboxPort outbox;
    private final IndicatorStrategy indicator;
    private final Map<AssetCode, AssetTracker> trackers = new ConcurrentHashMap<>();

    public AnalyticsService(
            TickHistoryPort ticks, SignalHistoryPort signals, SignalOutboxPort outbox,
            IndicatorStrategy indicator) {
        this.ticks = ticks;
        this.signals = signals;
        this.outbox = outbox;
        this.indicator = indicator;
    }

    @Transactional
    public void process(TickPriceRecord tick) {
        AssetTracker tracker = trackers.computeIfAbsent(
                tick.assetCode(), this::restoreTracker);
        if (!ticks.saveIfAbsent(tick)) {
            return;
        }
        synchronized (tracker) {
            AssetTracker.Snapshot before = tracker.snapshot();
            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        if (status != STATUS_COMMITTED) {
                            synchronized (tracker) {
                                tracker.restore(before);
                            }
                        }
                    }
                });
            }
            try {
                tracker.registrarPrecio(tick).ifPresent(signal -> {
                    signals.save(signal);
                    outbox.enqueue(signal.toPayload());
                });
            } catch (RuntimeException ex) {
                tracker.restore(before);
                throw ex;
            }
        }
    }

    private AssetTracker restoreTracker(AssetCode code) {
        long ticksSinceAlert = signals.latestTimestamp(code)
                .map(timestamp -> ticks.countSince(code, timestamp))
                .orElse(10L);
        int remainingCooldown = (int) Math.max(0L, 10L - ticksSinceAlert);
        return AssetTracker.rehydrate(code, indicator,
                ticks.recentPrices(code), remainingCooldown);
    }
}
