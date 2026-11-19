package edu.universidad.trading.live.application;

import edu.universidad.trading.contracts.TickPriceRecord;
import org.springframework.stereotype.Service;

@Service
public final class LiveTickerService {
    private final LiveTickPublisher publisher;

    public LiveTickerService(LiveTickPublisher publisher) {
        this.publisher = publisher;
    }

    public void onTick(TickPriceRecord tick) {
        publisher.publish(tick);
    }
}
