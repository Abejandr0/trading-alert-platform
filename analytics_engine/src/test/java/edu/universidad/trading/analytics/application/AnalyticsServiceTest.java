package edu.universidad.trading.analytics.application;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import edu.universidad.trading.contracts.AssetCode;
import edu.universidad.trading.contracts.TickPriceRecord;
import edu.universidad.trading.analytics.domain.SmaStrategy;
import edu.universidad.trading.analytics.domain.IndicatorStrategy;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {
    @Mock TickHistoryPort ticks;
    @Mock SignalHistoryPort signals;
    @Mock SignalOutboxPort outbox;
    @Spy SmaStrategy indicator = new SmaStrategy(5);
    @InjectMocks AnalyticsService service;

    @Test
    void storesEveryTickButEmitsOnlyOnMomentumCross() {
        AssetCode btc = new AssetCode("BTC");
        Instant start = Instant.parse("2026-01-01T00:00:00Z");
        when(ticks.saveIfAbsent(any())).thenReturn(true);

        for (int i = 0; i < 4; i++) {
            service.process(new TickPriceRecord(btc, new BigDecimal("100"), start.plusSeconds(i)));
        }
        verify(signals, never()).save(any());
        verifyNoInteractions(outbox);
        service.process(new TickPriceRecord(btc, new BigDecimal("130"), start.plusSeconds(4)));

        verify(ticks, times(5)).saveIfAbsent(any(TickPriceRecord.class));
        verify(signals).save(any());
        verify(outbox).enqueue(any());
    }

    @Test
    void failedOutboxWriteRestoresTrackerForRetry() {
        AssetCode btc = new AssetCode("BTC");
        Instant start = Instant.parse("2026-01-01T00:00:00Z");
        when(ticks.saveIfAbsent(any())).thenReturn(true);
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

    @Test
    void replayedTickDoesNotAdvanceTrackerOrDuplicateSignal() {
        AssetCode btc = new AssetCode("BTC");
        Instant start = Instant.parse("2026-01-01T00:00:00Z");
        when(ticks.saveIfAbsent(any())).thenReturn(true, true, true, true, true, false);
        for (int i = 0; i < 4; i++) {
            service.process(new TickPriceRecord(btc, new BigDecimal("100"), start.plusSeconds(i)));
        }
        TickPriceRecord spike = new TickPriceRecord(btc, new BigDecimal("130"), start.plusSeconds(4));

        service.process(spike);
        service.process(spike);

        verify(signals).save(any());
        verify(outbox).enqueue(any());
    }

    @Test
    void restoresSmaAndRemainingCooldownFromHistory() {
        AssetCode btc = new AssetCode("BTC");
        Instant lastAlert = Instant.parse("2026-01-01T00:00:04Z");
        when(ticks.recentPrices(btc)).thenReturn(List.of(
                new BigDecimal("100"), new BigDecimal("100"), new BigDecimal("100"), new BigDecimal("130")));
        when(signals.latestTimestamp(btc)).thenReturn(Optional.of(lastAlert));
        when(ticks.countSince(btc, lastAlert)).thenReturn(8L);
        when(ticks.saveIfAbsent(any())).thenReturn(true);

        service.process(new TickPriceRecord(btc, new BigDecimal("200"), lastAlert.plusSeconds(9)));
        service.process(new TickPriceRecord(btc, new BigDecimal("300"), lastAlert.plusSeconds(10)));
        verifyNoInteractions(outbox);
        service.process(new TickPriceRecord(btc, new BigDecimal("400"), lastAlert.plusSeconds(11)));

        verify(outbox).enqueue(any());
    }

    @Test
    void acceptsAnotherIndicatorThroughItsPort() {
        IndicatorStrategy fixedIndicator = new IndicatorStrategy() {
            @Override
            public int requiredSamples() {
                return 1;
            }

            @Override
            public Optional<BigDecimal> calculate(List<BigDecimal> prices) {
                return Optional.of(new BigDecimal("100"));
            }
        };
        AnalyticsService configurableService = new AnalyticsService(ticks, signals, outbox, fixedIndicator);
        when(ticks.saveIfAbsent(any())).thenReturn(true);

        configurableService.process(new TickPriceRecord(new AssetCode("BTC"), new BigDecimal("130"),
                Instant.parse("2026-01-01T00:00:00Z")));

        verify(signals).save(any());
    }
}
