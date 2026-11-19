package edu.universidad.trading.notification.infrastructure;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationRabbitConfiguration {
    @Bean
    Queue emailQueue() {
        return new Queue("email_queue", true);
    }

    @Bean
    FanoutExchange consoleAlertsExchange() {
        return new FanoutExchange("console_alerts", true, false);
    }
}
