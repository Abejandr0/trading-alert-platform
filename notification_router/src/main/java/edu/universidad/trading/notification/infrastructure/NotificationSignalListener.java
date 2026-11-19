package edu.universidad.trading.notification.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.universidad.trading.contracts.SignalPayloadRecord;
import edu.universidad.trading.notification.application.NotificationRouterService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public final class NotificationSignalListener {
    private final ObjectMapper objectMapper;
    private final NotificationRouterService service;

    public NotificationSignalListener(ObjectMapper objectMapper, NotificationRouterService service) {
        this.objectMapper = objectMapper;
        this.service = service;
    }

    @KafkaListener(topics = "trading_signals", groupId = "notification-router")
    public void receive(String json) throws JsonProcessingException {
        service.route(objectMapper.readValue(json, SignalPayloadRecord.class));
    }
}
