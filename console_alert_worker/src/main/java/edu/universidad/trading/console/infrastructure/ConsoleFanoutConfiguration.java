package edu.universidad.trading.console.infrastructure;

import java.util.Map;
import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsoleFanoutConfiguration {
    @Bean
    FanoutExchange consoleAlertsExchange() {
        return new FanoutExchange("console_alerts", true, false);
    }

    @Bean
    Queue consoleQueue() {
        return new AnonymousQueue(Map.of(
                "x-dead-letter-exchange", "notification_dead_letters",
                "x-dead-letter-routing-key", "console"));
    }

    @Bean
    Binding consoleBinding(Queue consoleQueue, FanoutExchange consoleAlertsExchange) {
        return BindingBuilder.bind(consoleQueue).to(consoleAlertsExchange);
    }
}
