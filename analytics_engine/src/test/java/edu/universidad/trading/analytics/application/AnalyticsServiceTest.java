package edu.universidad.trading.analytics.application;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import edu.universidad.trading.contracts.AssetCode;
import edu.universidad.trading.contracts.TickPriceRecord;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {
    @Mock TickHistoryPort ticks;
    @Mock SignalHistoryPort signals;
    @Mock SignalOutboxPort outbox;
    @InjectMocks AnalyticsService service;

    @Test
    void storesEveryTickButEmitsOnlyOnMomentumCross() {
        AssetCode btc = new AssetCode("BTC");
        Instant start = Instant.parse("2026-01-01T00:00:00Z");

        for (int i = 0; i < 4; i++) {
            service.process(new TickPriceRecord(btc, new BigDecimal("100"), start.plusSeconds(i)));
        }
        verifyNoInteractions(signals, outbox);
        service.process(new TickPriceRecord(btc, new BigDecimal("130"), start.plusSeconds(4)));

        verify(ticks, times(5)).save(any(TickPriceRecord.class));
        verify(signals).save(any());
        verify(outbox).enqueue(any());
    }

    @Test
    void failedOutboxWriteRestoresTrackerForRetry() {
        AssetCode btc = new AssetCode("BTC");
        Instant start = Instant.parse("2026-01-01T00:00:00Z");
        for (int i = 0; i < 4; i++) {
            service.process(new TickPriceRecord(btc, new BigDecimal("100"), start.plusSeconds(i)));
        }
        TickPriceRecord spike = new TickPriceRecord(btc, new BigDecimal("130"), start.plusSeconds(4));
        doThrow(new IllegalStateException("database unavailable")).when(outbox).enqueue(any());

        assertThrows(IllegalStateException.class, () -> service.process(spike));
        reset(outbox);
        service.process(spike);

        verify(outbox).enqueue(any());
        verify(signals, times(2)).save(any());
    }
}
