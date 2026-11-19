package edu.universidad.trading.notification.application;

import static org.mockito.Mockito.verify;

import edu.universidad.trading.contracts.AssetCode;
import edu.universidad.trading.contracts.SignalPayloadRecord;
import edu.universidad.trading.contracts.SignalType;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationRouterServiceTest {
    @Mock NotificationChannels channels;
    @Mock DispatchAuditPort audit;
    @InjectMocks NotificationRouterService service;

    @Test
    void dispatchesEachSignalToBothChannels() {
        SignalPayloadRecord signal = new SignalPayloadRecord(new AssetCode("BTC"),
                SignalType.BUY, "Momentum Crossover", Instant.parse("2026-01-01T00:00:00Z"));

        service.route(signal);

        verify(channels).enqueueEmail(signal);
        verify(channels).broadcastConsole(signal);
        verify(audit).record(signal, DispatchChannel.EMAIL);
        verify(audit).record(signal, DispatchChannel.CONSOLE);
    }
}
