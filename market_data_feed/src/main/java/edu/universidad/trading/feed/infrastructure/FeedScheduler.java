package edu.universidad.trading.feed.infrastructure;

import edu.universidad.trading.feed.application.MarketDataFeedService;
import java.time.Instant;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public final class FeedScheduler {
    private final MarketDataFeedService feed;

    public FeedScheduler(MarketDataFeedService feed) {
        this.feed = feed;
    }

    @Scheduled(fixedDelayString = "${feed.interval-ms:1000}")
    public void emit() {
        feed.emitAt(Instant.now());
    }
}
