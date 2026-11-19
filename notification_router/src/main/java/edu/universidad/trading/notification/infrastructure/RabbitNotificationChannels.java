package edu.universidad.trading.notification.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.universidad.trading.contracts.SignalPayloadRecord;
import edu.universidad.trading.notification.application.NotificationChannels;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public final class RabbitNotificationChannels implements NotificationChannels {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public RabbitNotificationChannels(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void enqueueEmail(SignalPayloadRecord signal) {
        rabbitTemplate.convertAndSend("", "email_queue", json(signal));
    }

    @Override
    public void broadcastConsole(SignalPayloadRecord signal) {
        rabbitTemplate.convertAndSend("console_alerts", "", json(signal));
    }

    private String json(SignalPayloadRecord signal) {
        try {
            return objectMapper.writeValueAsString(signal);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("cannot serialize signal", ex);
        }
    }
}
