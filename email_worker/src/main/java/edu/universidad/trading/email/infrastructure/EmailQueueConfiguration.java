package edu.universidad.trading.email.infrastructure;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailQueueConfiguration {
    @Bean
    Queue emailQueue() {
        return new Queue("email_queue", true);
    }
}
