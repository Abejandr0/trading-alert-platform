package edu.universidad.trading.console.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.universidad.trading.console.application.ConsoleAlertService;
import edu.universidad.trading.contracts.SignalPayloadRecord;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public final class ConsoleSignalListener {
    private final ObjectMapper objectMapper;
    private final ConsoleAlertService service;

    public ConsoleSignalListener(ObjectMapper objectMapper, ConsoleAlertService service) {
        this.objectMapper = objectMapper;
        this.service = service;
    }

    @RabbitListener(queues = "#{consoleQueue.name}")
    public void receive(String json) throws JsonProcessingException {
        service.onSignal(objectMapper.readValue(json, SignalPayloadRecord.class));
    }
}
