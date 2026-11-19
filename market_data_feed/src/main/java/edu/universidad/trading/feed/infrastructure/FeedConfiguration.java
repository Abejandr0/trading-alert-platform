package edu.universidad.trading.feed.infrastructure;

import edu.universidad.trading.contracts.AssetCode;
import edu.universidad.trading.feed.FeedProperties;
import edu.universidad.trading.feed.application.MarketDataFeedService;
import edu.universidad.trading.feed.application.TickPublisher;
import edu.universidad.trading.feed.domain.MarketScenario;
import edu.universidad.trading.feed.domain.ScenarioGenerator;
import java.util.List;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeedConfiguration {
    @Bean
    TopicExchange marketTicksExchange() {
        return new TopicExchange("market_ticks", true, false);
    }

    @Bean
    Queue feedBridgeTicksQueue() {
        return new Queue("bridge_ticks", true);
    }

    @Bean
    Binding feedBridgeTicksBinding(
            @Qualifier("feedBridgeTicksQueue") Queue feedBridgeTicksQueue,
            TopicExchange marketTicksExchange) {
        return BindingBuilder.bind(feedBridgeTicksQueue).to(marketTicksExchange).with("ticks.#");
    }

    @Bean
    ScenarioGenerator scenarioGenerator(FeedProperties properties) {
        return new ScenarioGenerator(properties.seed());
    }

    List<MarketScenario> marketScenarios(FeedProperties properties) {
        if (properties.scenarios() == null || properties.scenarios().isEmpty()) {
            throw new IllegalArgumentException("feed.scenarios must contain at least one asset");
        }
        return properties.scenarios().stream()
                .map(s -> new MarketScenario(new AssetCode(s.assetCode()), s.market(), s.basePrice(),
                        s.volatilityPercent(), s.spikeEveryTicks(), s.spikeMultiplier()))
                .toList();
    }

    @Bean
    MarketDataFeedService marketDataFeedService(
            ScenarioGenerator generator, FeedProperties properties, TickPublisher publisher) {
        return new MarketDataFeedService(generator, marketScenarios(properties), publisher);
    }
}
