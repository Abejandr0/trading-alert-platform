package edu.universidad.trading.live.infrastructure;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LiveRabbitConfiguration {
    @Bean
    TopicExchange liveMarketTicksExchange() {
        return new TopicExchange("market_ticks", true, false);
    }

    @Bean
    Queue liveTicksQueue() {
        return QueueBuilder.nonDurable("live_dashboard_ticks")
                .autoDelete()
                .ttl(5_000)
                .maxLength(50)
                .build();
    }

    @Bean
    Binding liveTicksBinding(Queue liveTicksQueue, TopicExchange liveMarketTicksExchange) {
        return BindingBuilder.bind(liveTicksQueue).to(liveMarketTicksExchange).with("ticks.#");
    }
}
