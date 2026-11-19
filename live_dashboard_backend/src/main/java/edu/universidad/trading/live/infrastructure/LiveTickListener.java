package edu.universidad.trading.live.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.universidad.trading.contracts.TickPriceRecord;
import edu.universidad.trading.live.application.LiveTickerService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public final class LiveTickListener {
    private final ObjectMapper objectMapper;
    private final LiveTickerService service;

    public LiveTickListener(ObjectMapper objectMapper, LiveTickerService service) {
        this.objectMapper = objectMapper;
        this.service = service;
    }

    @RabbitListener(queues = "live_dashboard_ticks")
    public void receive(String json) throws JsonProcessingException {
        service.onTick(objectMapper.readValue(json, TickPriceRecord.class));
    }
}
