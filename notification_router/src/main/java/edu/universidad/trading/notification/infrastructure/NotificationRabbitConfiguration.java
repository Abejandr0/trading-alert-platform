package edu.universidad.trading.notification.infrastructure;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationRabbitConfiguration {
    static final String DEAD_LETTER_EXCHANGE = "notification_dead_letters";
    static final String DEAD_LETTER_QUEUE = "notification_dead_letter_queue";

    @Bean
    Queue emailQueue() {
        return QueueBuilder.durable("email_queue")
                .deadLetterExchange(DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey("email")
                .build();
    }

    @Bean
    FanoutExchange consoleAlertsExchange() {
        return new FanoutExchange("console_alerts", true, false);
    }

    @Bean
    DirectExchange notificationDeadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE, true, false);
    }

    @Bean
    Queue notificationDeadLetterQueue() {
        return QueueBuilder.durable(DEAD_LETTER_QUEUE).build();
    }

    @Bean
    Binding emailDeadLetterBinding(@Qualifier("notificationDeadLetterQueue") Queue notificationDeadLetterQueue,
                                  DirectExchange notificationDeadLetterExchange) {
        return BindingBuilder.bind(notificationDeadLetterQueue)
                .to(notificationDeadLetterExchange).with("email");
    }

    @Bean
    Binding consoleDeadLetterBinding(@Qualifier("notificationDeadLetterQueue") Queue notificationDeadLetterQueue,
                                    DirectExchange notificationDeadLetterExchange) {
        return BindingBuilder.bind(notificationDeadLetterQueue)
                .to(notificationDeadLetterExchange).with("console");
    }
}
