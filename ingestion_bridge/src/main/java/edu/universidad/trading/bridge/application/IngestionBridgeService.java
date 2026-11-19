package edu.universidad.trading.bridge.application;

import edu.universidad.trading.contracts.TickPriceRecord;
import org.springframework.stereotype.Service;

@Service
public final class IngestionBridgeService {
    private final HistoricalTickPublisher publisher;

    public IngestionBridgeService(HistoricalTickPublisher publisher) {
        this.publisher = publisher;
    }

    public void forward(TickPriceRecord tick) {
        publisher.publish(tick);
    }
}
