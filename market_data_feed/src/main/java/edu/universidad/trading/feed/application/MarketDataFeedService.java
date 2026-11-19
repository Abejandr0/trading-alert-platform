package edu.universidad.trading.feed.application;

import edu.universidad.trading.feed.domain.MarketScenario;
import edu.universidad.trading.feed.domain.ScenarioGenerator;
import java.time.Instant;
import java.util.List;

public final class MarketDataFeedService {
    private final ScenarioGenerator generator;
    private final List<MarketScenario> scenarios;
    private final TickPublisher publisher;

    public MarketDataFeedService(ScenarioGenerator generator, List<MarketScenario> scenarios, TickPublisher publisher) {
        this.generator = generator;
        this.scenarios = List.copyOf(scenarios);
        this.publisher = publisher;
    }

    public void emitAt(Instant time) {
        for (MarketScenario scenario : scenarios) {
            publisher.publish(generator.next(scenario, time), scenario.routingKey());
        }
    }
}
