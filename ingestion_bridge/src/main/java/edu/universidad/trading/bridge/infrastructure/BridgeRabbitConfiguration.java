package edu.universidad.trading.bridge.infrastructure;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BridgeRabbitConfiguration {
    @Bean
    TopicExchange bridgeMarketTicksExchange() {
        return new TopicExchange("market_ticks", true, false);
    }

    @Bean
    Queue bridgeTicksQueue() {
        return new Queue("bridge_ticks", true);
    }

    @Bean
    Binding bridgeTicksBinding(Queue bridgeTicksQueue, TopicExchange bridgeMarketTicksExchange) {
        return BindingBuilder.bind(bridgeTicksQueue).to(bridgeMarketTicksExchange).with("ticks.#");
    }
}
