package edu.universidad.trading.bridge.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.universidad.trading.bridge.application.IngestionBridgeService;
import edu.universidad.trading.contracts.TickPriceRecord;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public final class BridgeTickListener {
    private final ObjectMapper objectMapper;
    private final IngestionBridgeService service;

    public BridgeTickListener(ObjectMapper objectMapper, IngestionBridgeService service) {
        this.objectMapper = objectMapper;
        this.service = service;
    }

    @RabbitListener(queues = "bridge_ticks")
    public void receive(String json) throws JsonProcessingException {
        service.forward(objectMapper.readValue(json, TickPriceRecord.class));
    }
}
