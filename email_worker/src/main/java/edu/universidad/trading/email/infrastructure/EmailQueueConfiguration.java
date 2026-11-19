package edu.universidad.trading.email.infrastructure;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailQueueConfiguration {
    @Bean
    Queue emailQueue() {
        return QueueBuilder.durable("email_queue")
                .deadLetterExchange("notification_dead_letters")
                .deadLetterRoutingKey("email")
                .build();
    }
}
